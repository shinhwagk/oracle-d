SELECT
    sys_context('USERENV','SESSION_USER') UNAME,
    sys_context('USERENV','INSTANCE_NAME') INSTNAME,
    '          ' CNAME,
    '          ' AUTHM,
    sys_context('USERENV','SERVER_HOST') SHOST
from dual