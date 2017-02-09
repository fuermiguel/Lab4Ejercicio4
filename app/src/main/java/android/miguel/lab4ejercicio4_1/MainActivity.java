package android.miguel.lab4ejercicio4_1;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Entity;
import android.provider.ContactsContract.RawContacts;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
//todo hay que crear un string e ir añadiendole la información en cada sitio


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView salida = (TextView) findViewById(R.id.salida);

        /*
            Obtenemos un cursor con los contactos
         */
        Cursor contactos = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        /*
        * Recorremos los contactos y creamos un Entity para cada contacto y así poder acceder a
        * sus rawcontacts y data a través de sus identificadores(ID_RAW, ID_DATA)
        */
        while (contactos.moveToNext()) {
            String cadena = "\n"
                    +"NOMBRE"
                    + "\n"
                    + contactos.getString(contactos.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY))
                    + "\n";
            /*
                Añadimos al uri el número id del contacto
             */
            Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI,
                    contactos.getString(contactos.getColumnIndex(Contacts._ID)));
            /*
                Añadimos el entity al ury para conformar el uri consultar
             */
            contactUri = Uri.withAppendedPath(contactUri,
                    Entity.CONTENT_DIRECTORY);
            
            /*
            Este cursor contiene la entity del contacto (solo un registro)
             */
            Cursor entidaContacto = getContentResolver().query(contactUri,
                    null, null, null, null);
            entidaContacto.moveToFirst();
            /*
            Obtenemos un cursor con los rawsContacts para un contact determinado(Puede ser más de un registro)
             */
            Cursor contactosRaw = getContentResolver().query(RawContacts.CONTENT_URI,
                    null,
                    RawContacts.CONTACT_ID + "=?",
                    new String[]{entidaContacto.getString(entidaContacto.getColumnIndex(Entity.RAW_CONTACT_ID))},
                    null);
            
            //Por cada rawContact hay que obtener los DATA desde un rawContactEntity(union de tablas rawContact y data)
            while (contactosRaw.moveToNext()) {
                cadena = cadena
                        + "NOMBRE CUENTA:"
                        + "\n"
                        + contactosRaw.getString(contactosRaw.getColumnIndex(RawContacts.ACCOUNT_NAME))
                        + "\n";
                /*
                    tenemos que conseguir el cursor entity de los contactosraw, dando valor a los
                    parámetros _ID=Contacts.Entity.RAW_CONTACT y DATA_ID = Contacts.Entity.DATA_ID
                 */

                 /*
                    Añadimos al uri el número id del contactoRAW
                */
                Uri contactRawUri = Uri.withAppendedPath(RawContacts.CONTENT_URI,
                        contactos.getString(contactos.getColumnIndex(Contacts._ID)));
                /*
                    Añadimos el entity al ury para conformar el uri consultar
                 */
                contactRawUri = Uri.withAppendedPath(contactRawUri,
                        RawContacts.Entity.CONTENT_DIRECTORY);

                /*
                    Aqui obtengo todos los detalles para un contacto y cuenta conocidas.
                 */
                Cursor entidadContactoRaw = getContentResolver().query(contactRawUri,
                        new String[]{
                                ContactsContract.RawContactsEntity._ID,
                                ContactsContract.RawContactsEntity.DATA_ID,
                                ContactsContract.RawContactsEntity.MIMETYPE,
                                ContactsContract.RawContactsEntity.DATA1,
                                ContactsContract.RawContactsEntity.DATA2,
                                ContactsContract.RawContactsEntity.DATA3},
                       //ContactsContract.RawContactsEntity._ID + "=?" + " AND " + ContactsContract.RawContactsEntity.DATA_ID + "=?",
                       // new String[]{entidaContacto.getString(entidaContacto.getColumnIndex(Entity.RAW_CONTACT_ID)),
                       //         entidaContacto.getString(entidaContacto.getColumnIndex(Entity.DATA_ID))},
                        null,
                        null,
                        null);
                try {
                    while (entidadContactoRaw.moveToNext()) {
                        String sourceId = entidadContactoRaw.getString(0);
                        if (!entidadContactoRaw.isNull(1)) {
                            String mimeType = entidadContactoRaw.getString(2);
                            String data1 = entidadContactoRaw.getString(3);
                            String data2 = entidadContactoRaw.getString(4);
                            String data3 = entidadContactoRaw.getString(4);

                            cadena =  cadena + "mimeType:" + "\n"
                                    + mimeType + "\n"
                                    + "DATA1" + "\n"
                                    + data1 + "\n"
                                    + "DATA2" + "\n"
                                    + data2 + "\n"
                                    + "DATA3" + "\n"
                                    + data3 + "\n";

                        }
                    }
                } finally {
                    entidadContactoRaw.close();
                }


                cadena = cadena + "\n***************FIN DE CUENTA******************\n";
            }
            salida.append(cadena);
            salida.append("\n***************FIN DE CONTACTO******************\n");
        }


    }
}
