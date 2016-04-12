package com.jfireframework.sql.function.mapper;

import com.jfireframework.sql.function.SqlSession;

/**
 * 用来给生成接口对象的类作为继承用
 * 方便在其中设置sqlSession
 * 
 * @author linbin
 *         
 */
public class Mapper
{
    protected SqlSession session;
    
    public void setSqlSession(SqlSession sqlSession)
    {
        this.session = sqlSession;
    }
    
}
