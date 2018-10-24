init:
    mysql -uroot -e "CREATE DATABASE IF NOT EXISTS buzz"
    mysql -uroot -e "SET @@session.time_zone = "+00:00";
