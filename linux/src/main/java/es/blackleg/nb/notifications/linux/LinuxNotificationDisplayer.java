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

import es.blackleg.jlibnotify.JLibnotify;
import es.blackleg.jlibnotify.JLibnotifyNotification;
import es.blackleg.jlibnotify.core.DefaultJLibnotifyLoader;
import es.blackleg.jlibnotify.exception.JLibnotifyInitException;
import es.blackleg.jlibnotify.exception.JLibnotifyLoadException;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hector Espert
 */
@ServiceProvider(service = LinuxNotificationDisplayer.class, position = 50)
public class LinuxNotificationDisplayer extends NotificationDisplayer {

    private static final Logger LOG = Logger.getLogger(LinuxNotificationDisplayer.class.getName());

    private static final String LIBNOTIFY = "libnotify.so.4";

    private static final String APP_NAME = "netbeans";

    private JLibnotify libnotify;

    public LinuxNotificationDisplayer() {
        try {
            this.libnotify = DefaultJLibnotifyLoader.init().load();
            LOG.log(Level.FINE, "Libnotify library loaded");
        } catch (JLibnotifyLoadException exception) {
            LOG.log(Level.WARNING, "Libnotify library not found", exception);
        }
    }

    private boolean isLoaded() {
        return Objects.nonNull(libnotify);
    }

    public void start() {
        if (!isLoaded()) {
            return;
        }
        
        if (libnotify.isInitted()) {
            return;
        }
        
        try {
            libnotify.init(APP_NAME);
            LOG.log(Level.FINE, "Libnotify initted");
        } catch(JLibnotifyInitException e) {
            LOG.log(Level.WARNING, "Unable to start libnotify");
        }
    }

    public void stop() {
        if (!isLoaded()) {
            return;
        }
        
        if (!libnotify.isInitted()) {
            return;
        }
        
        libnotify.unInit();
        LOG.log(Level.FINE, "Libnotify uninit");
    }

    @Override
    public Notification notify(String title, Icon icon, String detailsText, ActionListener detailsAction, Priority priority) {
        if (isLoaded() && libnotify.isInitted()) {
            JLibnotifyNotification notification = libnotify.createNotification(title, detailsText, null);
            notification.show();
        }

        return getDefaultNotificationDisplayer()
                .map(diplayer -> diplayer.notify(title, icon, detailsText, detailsAction, priority))
                .orElseGet(() -> new EmptyNotification());
    }

    @Override
    public Notification notify(String title, Icon icon, JComponent balloonDetails, JComponent popupDetails, Priority priority) {
        return getDefaultNotificationDisplayer()
                .map(diplayer -> diplayer.notify(title, icon, balloonDetails, popupDetails, priority))
                .orElseGet(() -> new EmptyNotification());
    }

    private Optional<NotificationDisplayer> getDefaultNotificationDisplayer() {
        return Lookup.getDefault()
                .lookupAll(NotificationDisplayer.class)
                .stream()
                .filter((notificationDisplayer) -> !LinuxNotificationDisplayer.class.isInstance(notificationDisplayer))
                .map(NotificationDisplayer.class::cast)
                .findAny();
    }

}
