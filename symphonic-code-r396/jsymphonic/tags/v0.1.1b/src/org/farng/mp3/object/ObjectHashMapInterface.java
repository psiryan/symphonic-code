package org.farng.mp3.object;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <p/>
 * Title: </p>
 * <p/>
 * <p/>
 * Description: </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2002 </p>
 * <p/>
 * <p/>
 * Company: </p>
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public interface ObjectHashMapInterface {

    public HashMap getIdToString();

    public HashMap getStringToId();

    public Iterator iterator();
}