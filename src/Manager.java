import net.xqj.exist.ExistXQDataSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import javax.xml.xquery.*;
import java.io.File;

/**
 * Created by bayer on 22/03/17.
 */
public class Manager {
    private static final String serverName = "localhost";
    private static final String serverPort = "8080";
    private static final String serverUser = "admin";
    private static final String serverPwd = "admin";

    private static String URI = "xmldb:exist://"+serverName+":"+serverPort+"/exist/xmlrpc";
    private static String driver = "org.exist.xmldb.DatabaseImpl";
    private Database database;
    private XQConnection xqConnection;

    public Manager(){
        try {
            Class cl = Class.forName(driver);
            database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");

            DatabaseManager.registerDatabase(database);

            XQDataSource xqs = new ExistXQDataSource();
            xqs.setProperty("serverName", serverName);
            xqs.setProperty("port", serverPort);
            xqConnection = xqs.getConnection();
        }catch(XQException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(IllegalAccessException e) {
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(XMLDBException e){
            e.printStackTrace();
        }
    }

    public Collection getCollection(String cUri) throws XMLDBException {
        return getOrCreateCollection(cUri, 0);
    }

    public void putResource(Collection col, File f, String name, String type) throws XMLDBException {
        Resource res = col.createResource(name, type);
        res.setContent(f);
        col.storeResource(res);
    }

    private Collection getOrCreateCollection(String cUri, int offset) throws XMLDBException {
        Collection col = DatabaseManager.getCollection(URI+cUri, serverUser, serverPwd);
        if(col == null){ // no exist Collection, go create!
            if(cUri.startsWith("/")){
                cUri = cUri.substring(1);
            }
            String paths[] = cUri.split("/");
            if(paths.length > 0){
                StringBuilder path = new StringBuilder();
                for (int i = 0; i <= offset; i++) {
                    path.append("/"+paths[i]);
                }
                Collection start = DatabaseManager.getCollection(URI+path, serverUser, serverPwd);
                if(start == null){
                    // no exist Collection, go create!
                    String parentPath = path.substring(0, path.lastIndexOf("/"));
                    Collection parent = DatabaseManager.getCollection(URI+parentPath, serverUser, serverPwd);
                    CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
                    col = mgt.createCollection(paths[offset]);
                    col.close();
                    parent.close();
                }else{
                    start.close();
                }
            }
            return getOrCreateCollection(cUri, ++offset);
        }else{
            return col;
        }
    }

    public XQResultSequence query(String query) throws XQException {
        XQPreparedExpression expression = xqConnection.prepareExpression(query);
        return expression.executeQuery();
    }

}
