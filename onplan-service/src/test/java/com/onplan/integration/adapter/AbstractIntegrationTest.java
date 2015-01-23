package com.onplan.integration.adapter;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {
    "/spring/applicationContext-resources.xml",
    "/spring/applicationContext-broker.xml",
    "/spring/applicationContext-beans.xml",
    "/spring/applicationContext-mongodb.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractIntegrationTest {
}
