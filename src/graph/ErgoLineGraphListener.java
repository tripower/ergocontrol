package graph;


import java.util.EventListener;

import data.ErgoData;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.02.2005
 * Time: 20:09:07
 * To change this template use File | Settings | File Templates.
 */
public interface ErgoLineGraphListener  extends EventListener {
    public void dataAvailable(ErgoLineGraphEvent evt, ErgoData data);
}
