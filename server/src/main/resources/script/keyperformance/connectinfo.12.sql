SELECT
    sys_context('USERENV','SESSION_USER') UNAME,
    sys_context('USERENV','INSTANCE_NAME') INSTNAME,
    sys_context('USERENV','CON_NAME') CNAME,
    sys_context('USERENV','AUTHENTICATION_METHOD') AUTH,
    sys_context('USERENV','SERVER_HOST') SHOST
from dual