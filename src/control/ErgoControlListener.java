package control;


import java.util.EventListener;
import java.util.Hashtable;

import data.ErgoBikeDefinition;
import data.ErgoData;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 17.02.2005
 * Time: 18:00:46
 * To change this template use File | Settings | File Templates.
 */
public interface ErgoControlListener extends EventListener {
    public void dataAvailable(ErgoControlEvent evt, ErgoData data, ErgoBikeDefinition bike, Hashtable<String,ErgoData> dataArray);
    public void finished(ErgoControlEvent evt, boolean bFailure);
}
