COPY USR (
        userId,
        password,
        email,
        name,
        dateOfBirth
)
FROM 'USR.csv'
DELIMTER ',' CSV HEADER
;

COPY Work_Ex (
        userId,
        company,
        role,
        location,
        startDate,
        endDate
)
FROM 'Work_Ex.csv'
DELIMTER ',' CSV HEADER
;

COPY Edu_Det(
        userid,
        institutionName,
        major,
        degree,
        startdate,
        enddate
)
FROM 'Edu_Det.csv'
DELIMITER "," CSV HEADER

COPY Message(
        messageid,
        senderid,
        receiverid,
        contents,
        sendTime,
        deleteStatus,
        status

)
FROM 'Message.csv'
DELIMITER "," CSV HEADER

COPY Connection(
        userid,
        connectionid,
        status
)
FROM 'Connection.csv'
DELIMITER "," CSV HEADER
