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
        category = "Help",
        id = "es.blackleg.nb.notifications.quote.InfoRandomNotificationAction"
)
@ActionRegistration(
        displayName = "#CTL_InfoRandomNotificationAction"
)
@ActionReference(path = "Menu/Tools/Notifications")
@Messages("CTL_InfoRandomNotificationAction=Yoda Quote (INFO)")
public final class InfoRandomNotificationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Faker faker = new Faker(Locale.ENGLISH);
        Image image = ImageUtilities.loadImage("org/netbeans/modules/notifications/resources/notifications.png");
        NotificationDisplayer.getDefault().notify("Yoda quote", ImageUtilities.image2Icon(image), faker.yoda().quote(), null);
    }
}
