CREATE SEQUENCE msgId_seq START WITH 27812;

CREATE OR REPLACE FUNCTION func()
        RETURNS "trigger" AS
        $BODY$
        BEGIN

        NEW.msgId := nextval('msgId_seq');
        RETURN NEW;

        END
        $BODY$
        LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT
ON MESSAGE FOR EACH ROW
EXECUTE PROCEDURE func();
