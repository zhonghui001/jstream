package com.jyb.core;

import com.jyb.config.JstreamConfiguration;

import java.util.HashMap;
import java.util.Map;

public interface StatementParser {

    public JstreamConfiguration parser(String sql);



}
