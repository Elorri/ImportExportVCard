package com.elorri.android.importexportvcard;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 5;
    private static final String TAG = "Exple";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.pick_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        Intent intent=getIntent();
        Log.e("Exple", Thread.currentThread().getStackTrace()[2] + ""+intent);

        Uri uri=intent.getData();

        if(uri != null){
            try {
                String total = IOUtils.toString( getContentResolver().openInputStream(uri));
                Log.e("Exple", Thread.currentThread().getStackTrace()[2] + ""+total);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

//    public void sendByBluetooth(){
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("text/x-vcard");
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(storage_path) );
//        startActivity(intent);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_CONTACT) {

            //On utilise l'uri pour récuperer l'id du contact. En fait on récupere la
            // lookup key car comme indiqué dans la doc il n'y a que la lookup key qui est
            // garantie de ne pas changer. L'id du contact lui peut changer
            Uri pickedUserUri = intent.getData();
            String lookup_key = pickedUserUri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(
                    pickedUserUri,
                    new String[]{ContactsContract.Contacts.LOOKUP_KEY}, null, null, null);
            cursor.moveToNext();
            lookup_key = cursor.getString(0);


            //On récupère toutes les données qui nous intéressent dans la table Data

            //On selectionne dans la tables data les lignes de ayant pour mimetype
            // 'structured_name' cad les données correspondant au nom et ayant pour lookup_key la
            // lookup key retrouvée plus haut.
            Cursor nameData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
                    },
                    ContactsContract.Data.MIMETYPE
                            + "=? and " + ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);

            Cursor compagnyData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            //Lequel des 2 mettre dans society ?
                            ContactsContract.CommonDataKinds.Organization.COMPANY,
                            ContactsContract.CommonDataKinds.Organization.TITLE,

                            //Si un jour on veut faire un traitement different selon le type
                            // d'adresse (Organization.HOME, Organization.WORK, etc)
                            ContactsContract.CommonDataKinds.Organization.TYPE
                    },
                    ContactsContract.Data.MIMETYPE
                            + "=? and " + ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);

            //Transformer nameData en YcontentValue et remplir bundle


            //On selectionne dans la tables data les lignes de ayant pour mimetype
            // 'structured_name' cad les données correspondant au nom et ayant pour lookup_key la
            // lookup key retrouvée plus haut.
            Cursor addressData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                            ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,

                            //Peut etre plus complete que que STREET, à voir
                            ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,

                            //Si un jour on veut faire un traitement different selon le type
                            // d'adresse (StructuredPostal.HOME, StructuredPostal.WORK,
                            // StructuredPostal.OTHER). Pour le moment la premiere adresse est
                            // mise en tant qu'adresse de facturation
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE
                    },
                    ContactsContract.Data.MIMETYPE
                            + "=? and " + ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);

            Cursor emailData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Email.ADDRESS,
                            //Si un jour on veut faire un traitement different selon le type
                            // d'adresse (Email.HOME, Email.WORK, Email.MOBILE, etc)
                            ContactsContract.CommonDataKinds.Email.TYPE
                    },
                    ContactsContract.Data.MIMETYPE + "=? and " +
                            ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);


            Cursor phoneData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            //Si un jour on veut faire un traitement different selon le type
                            // d'adresse (Phone.HOME, Phone.WORK, Phone.MOBILE, Phone.FAX etc)
                            ContactsContract.CommonDataKinds.Phone.TYPE
                    },
                    ContactsContract.Data.MIMETYPE + "=? and " +
                            ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);

            Cursor websiteData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Website.URL,
                            //Si un jour on veut faire un traitement different selon le type
                            // d'adresse (Website.HOME, Website.WORK, Website.PROFILE etc)
                            ContactsContract.CommonDataKinds.Website.TYPE
                    },
                    ContactsContract.Data.MIMETYPE + "=? and " +
                            ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);

            Cursor commentsData = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Note.NOTE,
                    },
                    ContactsContract.Data.MIMETYPE + "=? and " +
                            ContactsContract.Contacts.LOOKUP_KEY + "=?",
                    new String[]{
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE,
                            lookup_key},
                    null);

            Cursor civilityData; //Pas trouvé

//
//            Bundle user=new Bundle();
//            //user.putString("civility");
//            user.putString("adr_society", compagnyData.get);
//            user.putString("adr_firstname", nameData.get);
//            user.putString("adr_lastname", nameData.get);
//            user.putString("usr_adr_invoices", addressData.get);
//            user.putString("usr_adr_delivery", addressData.get);
//            user.putString("adr_address1", addressData.get);
//            user.putString("adr_address2", addressData.get);
//            user.putString("adr_postal_code", addressData.get);
//            user.putString("adr_city", addressData.get);
//            user.putString("adr_country", addressData.get);
//            user.putString("usr_email", emailData.get);
//            user.putString("adr_email", emailData.get);
//            user.putString("adr_phone", decideBestCandidate(phoneData.get);
//            user.putString("adr_mobile", phoneData.get);
//            user.putString("adr_fax", phoneData.get);
//            user.putString("usr_website", websiteData.get);
//            user.putString("comments", commentsData.get);
        }
    }

    public static File streamToFile(File directory, File fileName, InputStream in) {
        File file = createFile(directory, fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            Log.e(TAG, "An error occured while copying file stream", e);
        }
        return file;
    }

    public static File createFile(File directory, File file) {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            Log.e(TAG, "Unable to create directory for " + file.getName() + " export " + directory.getAbsolutePath());
            return null;
        }
        if (!file.isFile()) {
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                Log.e(TAG, "Something went wrong while creating " + file.getName() + " file", e);
                return null;
            }
        }
        return file;
    }
}
