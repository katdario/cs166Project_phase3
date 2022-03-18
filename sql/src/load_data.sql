COPY USR (
        userId,
        password,
        email,
        name,
        dateOfBirth
)
FROM 'USR.csv'
DELIMITER ',' CSV HEADER
;

COPY WORK_EXPR (
        userId,
        company,
        role,
        location,
        startDate,
        endDate
)
FROM 'work_ex.csv'
DELIMITER ',' CSV HEADER
;

COPY EDUCATIONAL_DETAILS(
        userId,
        institutionName,
        major,
        degree,
        startdate,
        enddate
)
FROM 'edu_det.csv'
DELIMITER ',' CSV HEADER
;

COPY MESSAGE(
        msgId,
        senderId,
        receiverId,
        contents,
        sendTime,
        deleteStatus,
        status

)
FROM 'message.csv'
DELIMITER ',' CSV HEADER
;

COPY CONNECTION_USR(
        userid,
        connectionid,
        status
)
FROM 'connection.csv'
DELIMITER ',' CSV HEADER
;
