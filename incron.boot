#!/bin/bash
#
#       /etc/rc.d/init.d/incron
#
#       A cron-like for inotify events.
#
# chkconfig: 345 99 01
# description: A cron-like for inotify events.

# Source function library.
. /etc/init.d/functions

LOCKFILE=/var/lock/subsys/incron

start() {
        echo -n "Starting incron: "
        daemon /usr/local/sbin/incrond
        RETVAL=$?
        [ $RETVAL -eq 0 ] && touch $LOCKFILE
        return $RETVAL
}

stop() {
        echo -n "Shutting down incron: "
        killproc incrond
        RETVAL=$?
        [ $RETVAL -eq 0 ] && rm -f $LOCKFILE
        return $RETVAL
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    *)
        echo "Usage: incron {start|stop|restart[|probe]"
        exit 1
        ;;
esac
exit $?
