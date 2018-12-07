package com.jyb.parser.antlr;

import com.jyb.parser.antlr.tree.Statement;

public class Main {

    public static void main(String[] args) {
        AntlrSqlParser antlrSqlParser = new AntlrSqlParser();
        //Statement statement = antlrSqlParser.
        //        createStatement("CREATE SOURCE TABLE TOPIC1( _TOPIC VARCHAR) WITH( TYPE = 'KAFKA') WATERMARK(id,'10 MINI')");
        //Statement statement = antlrSqlParser.createStatement("WITH A2 AS (SELECT * FROM TABLE2 WHERE ID=10) WATERMARK('ID','10 MINIT')");
        //Statement statement = antlrSqlParser.createStatement("SELECT * FROM TABLE2 WHERE ID=10");

        //Statement statement = antlrSqlParser.createStatement("WITH (A='A',B='B')INSERT INTO TABLEA (FILEA,FILEB) SELECT * FROM A");
        //Statement statement = antlrSqlParser.createStatement("with resources(A='A',B='B')");
        //Statement statement = antlrSqlParser.
        //        createStatement("CREATE SINK TABLE TOPIC1( _TOPIC VARCHAR) WITH( TYPE = 'KAFKA') WATERMARK('NIU','10 MINI')");

        //Statement statement = antlrSqlParser.createStatement("with (out_mode='append') insert into sink1 select window dt,count(1) co  from topic2 group by window(update_time, '10 minutes' )");
        Statement statement = antlrSqlParser.createStatement("with topic2 as( select cast(floor(cast(get_json_object(value,'$.update_time') as bigint)/1000) as timestamp) update_time, get_json_object(value,'$.ord_id') ord_id, get_json_object(value,'$.ord_amount') ord_amount from topic1 where get_json_object(value,'$.dbTable') ='t_order') watermark('update_time','10 minutes')");
        System.out.println(statement);
    }
}
