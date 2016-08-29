package com.hankarun.gevrek.model;

public class GroupMessage {
    public GroupMessage()
    {
        mMessage = new String[2];
    }
    public int    mGroupId;
    public String[] mMessage;
    public String mMessageDate;
    public String mMessageHeader;
    public String mAuthor;
    public int    mLevel;
}
