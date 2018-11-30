package com.jyb.parser.antlr;

import com.jyb.parser.antlr.tree.Statement;

public class Main {

    public static void main(String[] args) {
        AntlrSqlParser antlrSqlParser = new AntlrSqlParser();
        //Statement statement = antlrSqlParser.
        //        createStatement("CREATE SOURCE TABLE TOPIC1( _TOPIC VARCHAR) WITH( TYPE = 'KAFKA') WATERMARK(id,'10 MINI')");
        //Statement statement = antlrSqlParser.createStatement("WITH A2 AS (SELECT * FROM TABLE2 WHERE ID=10) WATERMARK('ID','10 MINIT')");
        //Statement statement = antlrSqlParser.createStatement("SELECT * FROM TABLE2 WHERE ID=10");
        //Statement statement = antlrSqlParser.createStatement("INSERT INTO TABLEA (FILEA,FILEB) SELECT * FROM A");
        Statement statement = antlrSqlParser.
                createStatement("CREATE SINK TABLE TOPIC1( _TOPIC VARCHAR) WITH( TYPE = 'KAFKA') WATERMARK('NIU','10 MINI')");
        System.out.println(statement);
    }
}
