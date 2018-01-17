-- begin OFFICE_REQUEST
create table OFFICE_REQUEST (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    APPLICANT_ID varchar(36),
    APPLICANT_CODE varchar(15),
    APPLICANT_PHONE varchar(100),
    SERIES varchar(10),
    NUMBER_ integer,
    DESCRIPTION varchar(100),
    STEP_ID varchar(36),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST

-- begin OFFICE_REQUEST_STEP
create table OFFICE_REQUEST_STEP (
    ID varchar(36) not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    POSITION_ID varchar(36),
    STATE varchar(50),
    USER_ID varchar(36),
    DESCRIPTION varchar(100),
    SUBMISSION_TERM date,
    SUBMITTED date,
    APPROVAL_TERM date,
    APPROVED date,
    PENALTY integer,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STEP
-- begin OFFICE_REQUEST_STEP_ACTION
create table OFFICE_REQUEST_STEP_ACTION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_STEP_ID varchar(36),
    TYPE_ varchar(50),
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(36),
    FILE_ID varchar(36),
    MESSAGE varchar(255),
    SUBMITTED date,
    APPROVED date,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STEP_ACTION
-- begin OFFICE_REQUEST_LOG
create table OFFICE_REQUEST_LOG (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    INFO varchar(255),
    SENDER_ID varchar(36),
    RECEPIENT_ID varchar(36),
    ATTACH_TYPE varchar(70),
    ATTACH_ID varchar(36),
    READ_ date,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_LOG
-- begin OFFICE_REQUEST_STEP_COMMUNICATION
create table OFFICE_REQUEST_STEP_COMMUNICATION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_STEP_ID varchar(36),
    QUESTION varchar(255),
    QUESTION_FILE_ID varchar(36),
    ANSWER varchar(255),
    ANSWER_FILE_ID varchar(36),
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STEP_COMMUNICATION
-- begin OFFICE_POSITION_ACTION
create table OFFICE_POSITION_ACTION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    POSITION_ID varchar(36),
    TYPE_ varchar(50) not null,
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(36),
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_POSITION_ACTION
-- begin OFFICE_POSITION
create table OFFICE_POSITION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    IDENTIFIER integer,
    DESCRIPTION varchar(100),
    DAYS_FOR_SUBMISSION integer,
    DAYS_FOR_APPROVAL integer,
    --
    primary key (ID)
)^
-- end OFFICE_POSITION
-- begin OFFICE_POSITION_USER
create table OFFICE_POSITION_USER (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    POSITION_ID varchar(36),
    USER_ID varchar(36),
    REQUESTS integer,
    THRESHOLD integer,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_POSITION_USER
