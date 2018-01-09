create table OFFICE_REQUEST_STEP_COMMUNICATION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    QUESTION varchar(255),
    QUESTION_FILE_ID varchar(36),
    ANSWER varchar(255),
    ANSWER_FILE_ID varchar(36),
    REQUEST_STEP_ID varchar(36),
    --
    primary key (ID)
);
