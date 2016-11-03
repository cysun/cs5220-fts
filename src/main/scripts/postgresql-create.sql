set default_text_search_config=english;

create table messages (
    id      serial primary key,
    subject varchar(4092),
    content text,
    author  varchar(255)
);

insert into messages (subject, content, author) values
    ('Today''s Weather', 'It is very code today.', 'Jane Doe');
insert into messages (subject, content, author) values
    ('Computer Books Sale', 'I''m selling some computer programming books. These books are pretty cheap.', 'Jane Doe');
insert into messages (subject, content, author) values
    ('Hello World', 'Hello-world is a computer program.', 'John Doe');

select to_tsvector( subject ) from messages;
select to_tsvector( content ) from messages;

select to_tsquery( 'computer & programs');
select to_tsquery( 'computer | programs');
select plainto_tsquery( 'computer programs');

-- Find the messages that contain "computer programs" in the content

select * from messages where plainto_tsquery('computer programs') @@ to_tsvector(content);

-- Find the messages that contain "computer programs" in either the subject or the content

select * from messages where plainto_tsquery('computer programs') @@ to_tsvector(subject)
	or plainto_tsquery('computer programs') @@ to_tsvector(content);

select m.* from messages m, plainto_tsquery('computer programs') q
    where q @@ to_tsvector(subject) or q @@ to_tsvector(content);

select m.* from messages m, plainto_tsquery('computer programs') q
    where q @@ (to_tsvector(subject) || to_tsvector(content));

-- Add a tsvector column
alter table messages add column tsv tsvector;

-- Populate the tsvector column
update messages set tsv = to_tsvector(subject) || to_tsvector(content);

-- The previous can now be simplified to query @@ tsv
select * from messages where plainto_tsquery('computer program') @@ tsv;

-- A trigger can be used to automatically populate the tsvector column.

create or replace function messages_ts_trigger_function() returns trigger as $$
begin
    new.tsv := to_tsvector(new.subject) || to_tsvector(new.content);
    return new;
end
$$ language plpgsql;

create trigger messages_ts_trigger
    before insert or update
    on messages
    for each row
    execute procedure messages_ts_trigger_function();

insert into messages (subject, content, author) values
    ('Computer Programmer Wanted', 'Salary is very high.', 'Tom Smith');

-- Create an index on the tsvector column.
create index messages_tsv_index
    on messages
    using gin(tsv);

-- Use setweight to give subject and content different weights
update messages set tsv = setweight(to_tsvector(subject), 'A') ||
    setweight(to_tsvector(content), 'D');

-- Update the trigger function to also use weights.
create or replace function messages_ts_trigger_function() returns trigger as $$
begin
    new.tsv := setweight(to_tsvector(new.subject), 'A') ||
        setweight(to_tsvector(new.content), 'D');
    return new;
end
$$ language plpgsql;

-- Use ts_rank() and ts_headline()
select ts_rank(tsv, q), ts_headline(m.subject,q), ts_headline(m.content,q)
	from messages m, plainto_tsquery('computer program') q
    where q @@ tsv;
