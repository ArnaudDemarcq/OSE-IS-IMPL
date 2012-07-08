/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl.util;

import java.util.Date;

/**
 *
 * @author arnaud
 */
public class UniqueId {

    public static final String getUniqueId(Object o) {
        Date currentDate = new Date();
        return "" + currentDate.getTime();
    }
}
