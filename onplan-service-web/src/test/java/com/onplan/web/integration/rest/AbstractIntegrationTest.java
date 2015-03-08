package com.onplan.web.integration.rest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {
    "/spring/applicationContext-resources.xml",
    "/spring/applicationContext-remoting.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractIntegrationTest {
}
