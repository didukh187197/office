-- begin OFFICE_REQUEST
create table OFFICE_REQUEST (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    --
    APPLICANT_ID varchar(32),
    APPLICANT_CODE varchar(15),
    APPLICANT_PHONE varchar(100),
    SERIES varchar(10),
    NUMBER_ integer,
    DESCRIPTION varchar(100),
    MOMENT bigint,
    STEP_ID varchar(32),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST
-- begin OFFICE_POSITION_ACTION
create table OFFICE_POSITION_ACTION (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    --
    POSITION_ID varchar(32),
    TYPE_ varchar(50) not null,
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(32),
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_POSITION_ACTION
-- begin OFFICE_REQUEST_STEP_ACTION
create table OFFICE_REQUEST_STEP_ACTION (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    --
    REQUEST_STEP_ID varchar(32),
    TYPE_ varchar(50),
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(32),
    FILE_ID varchar(32),
    MESSAGE varchar(255),
    SUBMITTED date,
    APPROVED date,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STEP_ACTION
-- begin OFFICE_POSITION
create table OFFICE_POSITION (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
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
-- begin OFFICE_REQUEST_STEP_COMMUNICATION
create table OFFICE_REQUEST_STEP_COMMUNICATION (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    --
    REQUEST_STEP_ID varchar(32),
    INITIATOR_ID varchar(32),
    QUESTION varchar(255),
    QUESTION_FILE_ID varchar(32),
    RECEPIENT_ID varchar(32),
    ANSWER varchar(255),
    ANSWER_FILE_ID varchar(32),
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STEP_COMMUNICATION
-- begin OFFICE_POSITION_USER
create table OFFICE_POSITION_USER (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    --
    POSITION_ID varchar(32),
    USER_ID varchar(32),
    REQUESTS integer,
    THRESHOLD integer,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_POSITION_USER
-- begin OFFICE_REQUEST_STEP
create table OFFICE_REQUEST_STEP (
    ID varchar(32),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    --
    REQUEST_ID varchar(32),
    POSITION_ID varchar(32),
    STATE varchar(50),
    USER_ID varchar(32),
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
-- begin OFFICE_REQUEST_LOG
create table OFFICE_REQUEST_LOG (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    --
    REQUEST_ID varchar(32),
    INFO varchar(255),
    SENDER_ID varchar(32),
    RECEPIENT_ID varchar(32),
    ATTACH_TYPE varchar(70),
    ATTACH_ID varchar(32),
    READ_ date,
    MOMENT bigint,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_LOG
