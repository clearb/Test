package app.wu.yoop.testapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.wu.yoop.testapp.service.LocalService;
import app.wu.yoop.testapp.service.RemoteService;
import app.wu.yoop.testapp.widget.PassView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PassView mPassView;
    TextView mTextView;
    TextView mTvContacts;
    TextView mTvExpandable;

    private Intent mLocalServiceIntent;
    private Intent mRemoteServiceIntent;

    private List<ContactsInfo> mContactsInfos;
    private List<MessageInfo> mMessageInfos;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPassView = (PassView) findViewById(R.id.passView);
        mTextView = (TextView) findViewById(R.id.tv);
        mTvContacts = (TextView) findViewById(R.id.tv_contacts);
        mTvExpandable = (TextView) findViewById(R.id.expand_text_view);
        mPassView.setIPassWordCallBack(new PassView.IPassWordCallBack() {
            @Override
            public void completeText(String content) {
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.tv_epub_read).setOnClickListener(this);
        File dir = getDir("", Context.MODE_PRIVATE);
        mTextView.setText(dir.getAbsolutePath());

        mLocalServiceIntent = new Intent(this, LocalService.class);
        mRemoteServiceIntent = new Intent(this, RemoteService.class);
        startService(mLocalServiceIntent);
        startService(mRemoteServiceIntent);
        mContactsInfos = new ArrayList<>();
        mMessageInfos = new ArrayList<>();
//        scanContacts();
//        scanMessage();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_epub_read:
                startActivity(new Intent(this, EpubReaderActivity.class));
                break;
            default:break;
        }
    }


    public class User{
        public String name;
        public String nick;
        public int age;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvExpandable.setText("看过我文章的人都知道，前段时间我报了舞蹈班，学习舞蹈，效果还不错。假期打算继续学习，这次就不报班，选择在家自己学习。因为基本功差不多的自己都会，服装道具也都有。虽然唱歌极其不好听，但我是真的很喜欢舞蹈啊。还有瑜伽，打算过段时间报班学习。之前跳舞时有个小伙伴练习瑜伽两年了，整个人气场都不同。就是超级有气质的那种女神级别人物，然后我就励志自己也要变成她那样的女神~");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mLocalServiceIntent);
        stopService(mRemoteServiceIntent);
    }

    private void scanMessage() {
        new ScanMessageTask().execute();
    }

    private void scanContacts() {
        new ScanContactsTask().execute();
    }

    private class ScanMessageTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(final Void... params) {
            mMessageInfos.clear();
            Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
            while (cursor.moveToNext()) {
                int columnIndexAddress = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                int columnIndexBody = cursor.getColumnIndex(Telephony.Sms.BODY);
                String address = cursor.getString(columnIndexAddress);
                String body = cursor.getString(columnIndexBody);
                MessageInfo info = new MessageInfo();
                info.setPhone(address);
                info.setContent(body);
                mMessageInfos.add(info);
            }
            cursor.close();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                StringBuilder sb = new StringBuilder();
                for (MessageInfo info : mMessageInfos) {
                    sb.append("\n").append(info.getPhone()).append(" ---> ").append(info.getContent());
                }
                mTvContacts.setText(sb.toString());
            }
        }
    }

    private class ScanContactsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(final Void... params) {
            mContactsInfos.clear();
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                int columnIndexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int columnIndexNo = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                int contactId = cursor.getInt(columnIndexNo);
                String name = cursor.getString(columnIndexName);
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId
                        , null, null);
                phones.moveToNext();
                String phone = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                ContactsInfo info = new ContactsInfo();
                info.setName(name);
                info.setPhone(phone);
                mContactsInfos.add(info);
                phones.close();
            }
            cursor.close();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                StringBuilder sb = new StringBuilder();
                for (ContactsInfo info : mContactsInfos) {
                    sb.append("\n").append(info.getName()).append(" ---> " + info.getPhone());
                }
                mTvContacts.setText(sb.toString());
            }
        }
    }

    private class ContactsInfo {
        private String name;
        private String phone;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(final String phone) {
            this.phone = phone;
        }
    }

    private class MessageInfo {
        String phone;
        String content;

        public String getPhone() {
            return phone;
        }

        public void setPhone(final String phone) {
            this.phone = phone;
        }

        public String getContent() {
            return content;
        }

        public void setContent(final String content) {
            this.content = content;
        }
    }
}
