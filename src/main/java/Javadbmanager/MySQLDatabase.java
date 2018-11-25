package Javadbmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

 class MySQLDatabase extends Database implements Backend {

    Logger logger = LogManager.getLogger();

    public MySQLDatabase() {
    }

    public MySQLDatabase(String hostname, String port, String username, String password, String database) {
        super(hostname, port, username, password, database);
    }




    public void  cleanDatabase(){
        ResultSet result = this.execute(    "SELECT concat('DROP TABLE IF EXISTS `', table_name, '`;')\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = '"+ this.getDatabase() +"';");
        List<String> deleteQueries = new ArrayList<>();



            try {
                deleteQueries.add("SET FOREIGN_KEY_CHECKS=0;");
                while (result.next()){
                    deleteQueries.add(result.getString(1));
                }
                deleteQueries.add("SET FOREIGN_KEY_CHECKS=1;");

                this.executeMultipleQueries(deleteQueries);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }




    }

    @Override
    public void rebuildDatabaseFromQueries(List<Query> queries) {
        this.cleanDatabase();
        this.executeMultipleQueries(queries.stream().map(Query::getQuery).collect(Collectors.toList()));
    }
}
