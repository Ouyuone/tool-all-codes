package com.oo.tools.comomon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        
        SourceClass source = new SourceClass();
        source.setId(1);
        source.setName("王五");
        source.setIsDelete(true);
        TargetClass target = new TargetClass();
     /*   BeanCopier.copyProperties(source, target,null,new HashMap<>(){
            {
                put("nickName","name");
            }
        });*/
        
       /* BeanCopier.copyProperties(source, target);*/
        
        /*BeanCopier.copyProperties(source, target, (s, t) -> {
            t.setNickName(s.getName());
        },null);*/
        BeanCopier.registerConverter(Boolean.class,String.class,(s)->{
            return s.toString();
        });
        StopWatch stopWatch = new StopWatch("BeanCopier:");
        stopWatch.start();
        for (int i = 0; i < 1000000; i++) {
            BeanCopier.copyProperties(source, target);
        }
        stopWatch.stop();
        System.out.println( stopWatch.prettyPrint());
        System.out.println("source: "+source);
        System.out.println("target: "+target);
        assertTrue( true);
    }
}
