/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.GroupChats.ShowGroupMembers.Background;

import java.util.List;

public interface OnGroupMembersLoadListener
{
    void onGroupMembersLoaded(List<Object> data);
}
