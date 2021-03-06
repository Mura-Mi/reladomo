/*
 Copyright 2016 Goldman Sachs.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package com.gs.fw.common.mithra.cache;

import com.gs.fw.common.mithra.MithraObjectFactory;
import com.gs.fw.common.mithra.MithraObjectPortal;
import com.gs.fw.common.mithra.attribute.Attribute;
import com.gs.fw.common.mithra.extractor.Extractor;
import com.gs.fw.common.mithra.notification.listener.MithraNotificationListener;
import com.gs.fw.common.mithra.notification.listener.PartialCacheMithraNotificationListener;




public class PartialNonDatedTransactionalCache extends AbstractNonDatedTransactionalCache implements ReferenceListener
{
    private PartialCacheMithraNotificationListener notificationListener;
    
    public PartialNonDatedTransactionalCache(Attribute[] pkAttributes, MithraObjectFactory factory, long timeToLive, long relationshipTimeToLive)
    {
        super(pkAttributes, factory, timeToLive, relationshipTimeToLive);
        MithraReferenceThread.getInstance().addListener(this);
    }

    public PartialNonDatedTransactionalCache(Attribute[] pkAttributes, MithraObjectFactory factory, Attribute[] immutableAttributes, long timeToLive, long relationshipTimeToLive)
    {
        super(pkAttributes, factory, immutableAttributes, timeToLive, relationshipTimeToLive);
        MithraReferenceThread.getInstance().addListener(this);
    }

    @Override
    protected Index createIndex(String indexName, Extractor[] extractors)
    {
        return null;
    }

    @Override
    protected PrimaryKeyIndex createPrimaryKeyIndex(String indexName, Extractor[] extractors, long timeToLive, long relationshipTimeToLive)
    {
        return new TransactionalPartialUniqueIndexWithWeakBacking(indexName, extractors, timeToLive, relationshipTimeToLive);
    }

    @Override
    protected Index createUniqueIndex(String indexName, Extractor[] extractors, long timeToLive, long relationshipTimeToLive)
    {
        return new TransactionalPartialUniqueIndex(indexName, extractors, timeToLive, relationshipTimeToLive);
    }

    public boolean isFullCache()
    {
        return false;
    }

    public boolean isPartialCache()
    {
        return true;
    }

    public MithraNotificationListener createNotificationListener(MithraObjectPortal portal)
    {
        PartialCacheMithraNotificationListener local = this.notificationListener;
        if (local == null || local.getMithraObjectPortal() != portal)
        {
            local = new PartialCacheMithraNotificationListener(portal);
            notificationListener = local;
        }
        return local;
    }

    @Override
    public void destroy()
    {
        super.destroy();
        MithraReferenceThread.getInstance().removeListener(this);
    }
}
