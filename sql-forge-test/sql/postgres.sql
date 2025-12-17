CREATE TABLE IF NOT EXISTS score
(
    id integer NOT NULL,
    student_id integer NOT NULL,
    grade integer NOT NULL,
    PRIMARY KEY (id)
    )

    insert into score(id,student_id,grade)values(1,1,80);
insert into score(id,student_id,grade)values(2,2,90);