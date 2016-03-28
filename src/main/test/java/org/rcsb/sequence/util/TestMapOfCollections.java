package org.rcsb.sequence.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tara on 3/24/16.
 */
public class TestMapOfCollections {

    @Test


    public void testRemoveKey() throws Exception {

        MapOfCollections moc = new MapOfCollections(HashMap.class, ArrayList.class);

        HashMap<Integer,String> map1 = new HashMap<Integer,String>();
        map1.put(1,"One");
        map1.put(2,"Two");

        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("First");
        list1.add("Second");



        HashMap<Integer,String> map2 = new HashMap<Integer,String>();
        map2.put(3,"Three");
        map2.put(4,"Four");

        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("Third");
        list2.add("Fourth");


        moc.put(map1,list1);
        moc.put(map2,list2);

        //moc.removeKeyValue(map2,"Third");

        junit.framework.TestCase.assertNotNull(moc);
        junit.framework.TestCase.assertEquals(2, moc.size());

    }
}
