package com.authrus.common.manage;

import java.util.List;

public interface ObjectPath {
   String getObjectName();
   List<ObjectId> getObjectPath();
   ObjectPath getRelativePath(ObjectId objectId);
}
