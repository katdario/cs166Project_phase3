-- ==================================
-- USR Indexes

CREATE INDEX USR_userId_index
ON USR 
USING hash(userId);

CREATE INDEX USR_password_index
ON USR 
USING hash(password);

CREATE INDEX USR_email_index
ON USR 
USING hash(email);

CREATE INDEX USR_name_index
ON USR 
USING btree(userId);

CREATE INDEX USR_dateOfBirth_index
ON USR 
USING hash(dateOfBirth);

-- ===================================
-- WORK_EXPR Indexes

CREATE INDEX WORK_EXPR_userId_index
ON WORK_EXPR
USING hash(userId);

CREATE INDEX WORK_EXPR_company_index
ON WORK_EXPR
USING hash(company);

CREATE INDEX WORK_EXPR_role_index
ON WORK_EXPR
USING hash(role);

CREATE INDEX WORK_EXPR_location_index
ON WORK_EXPR
USING hash(location);

CREATE INDEX WORK_EXPR_startDate_index
ON WORK_EXPR
USING btree(startDate);

CREATE INDEX WORK_EXPR_endDate_index
ON WORK_EXPR
USING btree(endDate);

-- ===================================
-- EDUCATIONAL_DETAILS

CREATE INDEX EDUCATIONAL_DETAILS_userId_index
ON EDUCATIONAL_DETAILS
USING hash(userId);

CREATE INDEX EDUCATIONAL_DETAILS_institutionName_index
ON EDUCATIONAL_DETAILS
USING btree(institutionName);

CREATE INDEX EDUCATIONAL_DETAILS_major_index
ON EDUCATIONAL_DETAILS
USING hash(major);

CREATE INDEX EDUCATIONAL_DETAILS_degree_index
ON EDUCATIONAL_DETAILS
USING hash(degree);

CREATE INDEX EDUCATIONAL_DETAILS_startdate_index
ON EDUCATIONAL_DETAILS
USING btree(startdate);

CREATE INDEX EDUCATIONAL_DETAILS_enddate_index
ON EDUCATIONAL_DETAILS
USING btree(enddate);

-- ===================================
-- MESSAGE

CREATE INDEX MESSAGE_msgId_index
ON MESSAGE
USING hash(msgId);

CREATE INDEX MESSAGE_senderId_index
ON MESSAGE
USING hash(senderId);

CREATE INDEX MESSAGE_receiverId_index
ON MESSAGE
USING hash(receiverId);

CREATE INDEX MESSAGE_sendTime_index
ON MESSAGE
USING btree(sendTime);

CREATE INDEX MESSAGE_deleteStatus_index
ON MESSAGE
USING hash(deleteStatus);

CREATE INDEX MESSAGE_status_index
ON MESSAGE
USING hash(status);

-- ===================================
-- CONNECTION_USR

CREATE INDEX CONNECTION_USR_userId_index
ON CONNECTION_USR
USING hash(userId);

CREATE INDEX CONNECTION_USR_connectionId_index
ON CONNECTION_USR
USING hash(connectionId);

CREATE INDEX CONNECTION_USR_status_index
ON CONNECTION_USR
USING hash(status);



