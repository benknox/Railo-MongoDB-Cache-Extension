package railo.extension.io.cache.mongodb;

import com.mongodb.*;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.type.Struct;
import railo.runtime.util.Cast;

import java.util.ArrayList;
import java.util.List;

public class MongoConnection {

    private static MongoClient instance;

    private MongoConnection(){}

    public static MongoClient init(Struct arguments){

        if(instance != null){
            return instance;
        }

        CFMLEngine engine = CFMLEngineFactory.getInstance();
        Cast caster = engine.getCastUtil();
        MongoOptions opts = new MongoClientOptions().builder();
        List<ServerAddress> addr = new ArrayList<ServerAddress>();

        try{
            //options
            opts.connectionsPerHost( caster.toIntValue(arguments.get("connectionsPerHost")) );
            // Add SSL?
            (if arguments.get("ssl") == true)
                opts.socketFactory( new SSLSocketFactory().getDefault() );

            String[] hosts = caster.toString(arguments.get("hosts")).split("\\n");

            for (int i = 0; i < hosts.length; i++) {
                addr.add(new ServerAddress(hosts[i]));
            }

            //create the mongo instance
            try {
                instance = new MongoClient(addr, opts);
            } catch (MongoException e) {
                e.printStackTrace();
            }

            /* authenticate if required*/
            String username = caster.toString(arguments.get("username"));
            char[] password = caster.toString(arguments.get("password")).toCharArray();
            String database = caster.toString(arguments.get("database"));
            instance.getDB(database).authenticate(username, password);

        }catch(Exception e){
            e.printStackTrace();
        }

        return instance;
    }

    public static MongoClient getInstance(){
        return instance;
    }

}
