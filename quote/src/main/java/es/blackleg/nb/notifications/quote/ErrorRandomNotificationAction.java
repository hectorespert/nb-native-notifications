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
package es.blackleg.nb.notifications.quote;

import com.github.javafaker.Faker;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "es.blackleg.nb.notifications.quote.ErrorRandomNotificationAction"
)
@ActionRegistration(
        displayName = "#CTL_ErrorRandomNotificationAction"
)
@ActionReference(path = "Menu/Tools/Notifications", position = 3333)
@Messages("CTL_ErrorRandomNotificationAction=Chuck Norris fact (ERROR)")
public final class ErrorRandomNotificationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Faker faker = new Faker(Locale.ENGLISH);
        Image image = ImageUtilities.loadImage("org/netbeans/modules/notifications/resources/notificationsError.png");
        NotificationDisplayer.getDefault().notify("Chuck Norris fact", ImageUtilities.image2Icon(image), faker.chuckNorris().fact(), null, NotificationDisplayer.Priority.HIGH, NotificationDisplayer.Category.ERROR);
    }
}
