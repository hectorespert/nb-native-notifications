/*
 * Copyright 2020 Hector Espert.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.blackleg.nb.notifications.linux;

import javax.swing.JComponent;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.openide.awt.Notification;

/**
 *
 * @author Hector Espert
 */
@EnabledOnOs({OS.LINUX})
public class LinuxNotificationDisplayerTest {
    
    private LinuxNotificationDisplayer notificationDisplayer;


    @BeforeEach
    public void setUp() {
        notificationDisplayer = new LinuxNotificationDisplayer();
        notificationDisplayer.start();
        
    }

    @AfterEach
    public void tearDown() {
        notificationDisplayer.stop();
    }

    @Test
    public void testNotify() {
        Notification notification = notificationDisplayer.notify("title", null, "details", null, null);
        assertNotNull(notification);
        assertEquals(EmptyNotification.class, notification.getClass());
    }

    @Test
    public void testNotifyWithJComponents() {
        Notification notification = notificationDisplayer.notify("title", null, (JComponent) null, (JComponent) null, null);
        assertNotNull(notification);
        assertEquals(EmptyNotification.class, notification.getClass());
    }
    
}
