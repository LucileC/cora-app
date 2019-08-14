package com.example.coraapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatsFragment mChatsFragment = new ChatsFragment();
                return mChatsFragment;
            case 1:
                GroupsFragment mGroupsFrangment = new GroupsFragment();
                return  mGroupsFrangment;
            case 2:
                ContactsFragment mContactsFragment = new ContactsFragment();
                return  mContactsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Chats";
            case 1:
                return  "Groups";
            case 2:
                return  "Contacts";
            default:
                return null;
        }
    }
}
