package Javadbmanager;

import java.util.List;

 interface Backend {

    void rebuildDatabaseFromQueries(List<Query> queries);

}
