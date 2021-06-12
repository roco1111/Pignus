package com.rosario.hp.pignus.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rosario.hp.pignus.MainActivity;
import com.rosario.hp.pignus.include.Constantes;
import com.rosario.hp.pignus.include.DialogUtils;
import com.rosario.hp.pignus.R;
import com.rosario.hp.pignus.include.VolleySingleton;
import com.rosario.hp.pignus.notificaciones.LoginInteractor;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import com.android.volley.Request;
import androidx.annotation.NonNull;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.DefaultRetryPolicy;

import com.rosario.hp.pignus.include.NothingSelectedSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import java.util.Map;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class fragment_presentacion extends Fragment implements LoginInteractor.Callback{
    private Button ingreso;
    private Button registro;
    private TextView tvNombre = null;
    private TextView tvClave = null;
    private TextView tvConfirmacion = null;
    private TextView tvMail = null;
    private Button editar_foto;
    private static final String TAG = fragment_presentacion.class.getSimpleName();
    public static final String ARG_ARTICLES_NUMBER = "fragment_presentacion";
    private CallbackManager callbackManager;
    AccessToken accessToken;
    private AuthCredential credential;
    private JsonObjectRequest myRequest;
    private Gson gson = new Gson();
    private String ls_cod_empleado;
    private ProgressDialog m_Dialog = null;
    private String ls_contrasena;
    private String ls_nombre;
    private String ls_mail;
    private TextView olvidaste;
    private String ls_confirmacion;
    private Dialog alerta;
    Toast toast1;
    private LoginInteractor.Callback mCallback;
    private Dialog mIngreso;
    private Dialog mRegistro;
    DatePickerDialog datePickerDialog;
    private FirebaseAuth mFirebaseAuth;
    String id_firebase;
    NothingSelectedSpinnerAdapter nothing;
    private FirebaseAuth mAuth = null;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    Activity act;
    StorageReference storageRef;
    private FirebaseStorage storage;
    private Bitmap loadedImage;
    Uri imageUri;
    Context context;
    private CircleImageView imagen;
    private Button configuracion;


    public fragment_presentacion() {}

    @Override
    public void onAuthFailed(String msg) {
        this.showLoginError(msg);
    }
    @Override
    public void onBeUserResolvableError(int errorCode) {
    }
    @Override
    public void onEmailError(String msg) {
        this.setEmailError(msg);
    }

    @Override
    public void onPasswordError(String msg) {
        this.setPasswordError(msg);
    }

    @Override
    public void onAuthSuccess() {
    }
    @Override
    public void onGooglePlayServicesFailed() {
        this.showGooglePlayServicesError();
    }
    @Override
    public void onNetworkConnectFailed() {
    }

    public void setEmailError(String error) {

    }


    public void showLoginError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public void setPasswordError(String error) {

    }


    public void showGooglePlayServicesError() {
        Toast.makeText(getActivity(),
                "Se requiere Google Play Services para usar la app", Toast.LENGTH_LONG)
                .show();
    }

    public void showNetworkError() {
        Toast.makeText(getActivity(),
                "La red no está disponible. Conéctese y vuelva a intentarlo", Toast.LENGTH_LONG)
                .show();
    }


    public interface Callback {
        void onInvokeGooglePlayServices(int codeError);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.act = getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_presentacion, container, false);
        this.ingreso =  v.findViewById(R.id.buttonIngreso) ;
        this.registro =  v.findViewById(R.id.buttonRegistro);


        ///FIREBASE////
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        this.registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRegistro = createRegistroDialogo();

                mRegistro.show();

            }
        });

        this.ingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIngreso = createLoginDialogo();

                mIngreso.show();
            }
        });

        return v;
    }


    public void cargarDatos(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        map.put("mail", ls_mail);
        map.put("puesto", "1");

        JSONObject jobject = new JSONObject(map);


        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8"));
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_BY_CLAVE + "?" + encodedParams;
        Log.d(TAG,newURL);

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuesta(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String estado = response.getString("estado");

            switch (estado) {
                case "1":

                    JSONArray mensaje = response.getJSONArray("empleados");

                    for(int i = 0; i < mensaje.length(); i++) {
                        JSONObject object = mensaje.getJSONObject(i);

                        ls_cod_empleado = object.getString("id");
                        ls_nombre = object.getString("nombre");

                    }

                    actualizar_token(id_firebase);

                    break;

                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;

                case "3":
                    String mensaje3 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public AlertDialog createRegistroDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.activity_datos_usuario, null);


        final Context context = getContext();

        builder.setView(v);

        tvNombre = v.findViewById(R.id.editTextNombre);
        tvClave = v.findViewById(R.id.editTextClave);
        tvConfirmacion = v.findViewById(R.id.editTextConfirmar);
        tvMail = v.findViewById(R.id.editTextMail);


        Button signup = v.findViewById(R.id.buttonRegistro);

        String rec = getActivity().getResources().getString(R.string.menu_guardar);
        signup.setText(rec);

        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ls_contrasena = tvClave.getText().toString();
                        ls_confirmacion = tvConfirmacion.getText().toString();
                        ls_nombre = tvNombre.getText().toString();
                        ls_mail = tvMail.getText().toString();
                        Boolean lb_compara;

                        if( ls_nombre.equals(""))
                        {
                            tvNombre.setHintTextColor(Color.RED);
                            tvNombre.requestFocus();
                            toast1 = Toast.makeText(getContext(), "Debe ingresar su nombre... " , Toast.LENGTH_LONG);
                            toast1.setGravity(19, 0, 0);
                            toast1.show();
                            return;

                        }else {
                            tvNombre.setHintTextColor(getResources().getColor(R.color.colorRippleMini));
                        }

                        if( ls_mail.equals(""))
                        {
                            tvMail.setHintTextColor(Color.RED);
                            tvMail.requestFocus();
                            toast1 = Toast.makeText(getContext(), "Debe ingresar su mail... " , Toast.LENGTH_LONG);
                            toast1.setGravity(19, 0, 0);
                            toast1.show();
                            return;

                        }else {
                            tvMail.setHintTextColor(getResources().getColor(R.color.colorRippleMini));
                        }


                        if( ls_contrasena.equals("")){
                            tvClave.setHintTextColor(Color.RED);
                            tvClave.requestFocus();
                            toast1 = Toast.makeText(getContext(), "Debe ingresar contraseña... " , Toast.LENGTH_LONG);
                            toast1.setGravity(19, 0, 0);
                            toast1.show();
                            return;
                        }else {
                            tvClave.setHintTextColor(getResources().getColor(R.color.colorRippleMini));
                        }

                        if( ls_confirmacion.equals("")){
                            tvConfirmacion.setHintTextColor(Color.RED);
                            tvConfirmacion.requestFocus();
                            toast1 = Toast.makeText(getContext(), "Debe ingresar la confirmación de su clave... " , Toast.LENGTH_LONG);
                            toast1.setGravity(19, 0, 0);
                            toast1.show();
                            return;
                        }else {
                            tvConfirmacion.setHintTextColor(getResources().getColor(R.color.colorRippleMini));
                        }
                        lb_compara = compara_clave(ls_contrasena, ls_confirmacion);
                        if(!lb_compara){
                            toast1 = Toast.makeText(getContext(), "La clave ingresada no coincide con su confirmación" , Toast.LENGTH_LONG);
                            toast1.setGravity(19, 0, 0);
                            toast1.show();
                            return;
                        }

                        final String clave = md5(tvClave.getText().toString());
                        final String clave_original = tvClave.getText().toString();
                        final String mail = tvMail.getText().toString();
                        //firebase
                        mAuth.addAuthStateListener(mAuthListener);
                        mAuth.createUserWithEmailAndPassword(mail, clave_original)
                                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            guardarEmpleado( context);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            String ls_error;
                                            try {
                                                throw task.getException();
                                            } catch(FirebaseAuthWeakPasswordException e) {
                                                ls_error = getString(R.string.error_weak_password);
                                                tvClave.requestFocus();
                                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                                ls_error = getString(R.string.error_invalid_email);
                                                tvMail.requestFocus();
                                            } catch(Exception e) {
                                                Log.e(TAG, e.getMessage());
                                                ls_error = e.getMessage();
                                            }

                                            Toast.makeText(
                                                    getActivity(),
                                                    ls_error,
                                                    Toast.LENGTH_LONG).show();

                                        }
                                    }


                                });


                    }

                }
        );



        return builder.create();
    }

    private class agregar_empleado extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private String mUrl;



        public agregar_empleado(String url,Context context) {
            mContext = context;
            mUrl = url;
        }


        @Override
        protected void onPreExecute() {
        }


        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            voley(mUrl,mContext);
        }
    }



    public void guardarEmpleado(final Context context) {


        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "createUserWithEmail:success");

        id_firebase =  mAuth.getCurrentUser().getIdToken(true).toString();



        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        map.put("nombre", ls_nombre);
        map.put("mail", ls_mail);
        map.put("id_firebase", id_firebase);
        map.put("clave", ls_contrasena);
        map.put("puesto", "1");
        JSONObject jobject = new JSONObject(map);


        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8"));
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));

        String newURL = Constantes.INSERT_EMPLEADO + "?" + encodedParams;

        Log.d(TAG,newURL);

        voley(newURL,context);

    }

    private void voley(String newURL,final Context context){
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        //jobject,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor

                                procesarRespuestaGuardar(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }


                ) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }


                }

        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaGuardar(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":

                    cargarDatos(context);

                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean compara_clave(String clave, String confirma){


        if (clave.equals(confirma)) {
            return true;
        }else{
            return  false;
        }

    }

    public static void main(String[] args) {

        String s = "SecretKey20111013000";
        String  res = md5(s);
        System.out.println(res);

    }

    private static String md5(String s) { try {

        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
        return "";

    }
    public AlertDialog createLoginDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.login, null);


        builder.setView(v);

        tvMail =  v.findViewById(R.id.editTextUsuario);
        tvClave =  v.findViewById(R.id.editTextClave);
        olvidaste =  v.findViewById(R.id.olvidaste);

        Button signin =  v.findViewById(R.id.buttonIngreso);

        olvidaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"olvidaste en onClick");
                alerta = onCreateDialog(tvMail.getText().toString());

                alerta.show();
            }
        });



        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"en onClick");
                        ls_contrasena = tvClave.getText().toString();
                        ls_mail = tvMail.getText().toString();
                        m_Dialog = DialogUtils.showProgressDialog(getActivity(),"Iniciando sesión..");

                        if( ls_mail.equals("")){
                            m_Dialog.dismiss();
                            Log.d(TAG ,"email_vacio");
                            fragment_presentacion.this.toast1 = Toast.makeText(fragment_presentacion.this.getContext(), "Debe ingresar usuario... " , Toast.LENGTH_LONG);
                            fragment_presentacion.this.toast1.setGravity(19, 0, 0);
                            fragment_presentacion.this.toast1.show();
                            return;
                        }

                        if( ls_contrasena.equals("")){
                            m_Dialog.dismiss();
                            Log.d(TAG ,"pass_vacia");
                            fragment_presentacion.this.toast1 = Toast.makeText(fragment_presentacion.this.getContext(), "Debe ingresar contraseña... " , Toast.LENGTH_LONG);
                            fragment_presentacion.this.toast1.setGravity(19, 0, 0);
                            fragment_presentacion.this.toast1.show();
                            return;
                        }

                        mFirebaseAuth.signInWithEmailAndPassword(ls_mail, ls_contrasena)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (!task.isSuccessful()) {

                                            try {
                                                throw task.getException();

                                            } catch (FirebaseAuthInvalidUserException e) {
                                                Log.d(TAG, "Invalid Emaild Id - email :" + ls_mail);
                                                fragment_presentacion.this.toast1 = Toast.makeText(fragment_presentacion.this.getContext(), "Error al ingresar mail... ", Toast.LENGTH_LONG);
                                                fragment_presentacion.this.toast1.setGravity(19, 0, 0);
                                                fragment_presentacion.this.toast1.show();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                Log.d(TAG, "Invalid Password - email :" + ls_mail);
                                                fragment_presentacion.this.toast1 = Toast.makeText(fragment_presentacion.this.getContext(), "Error al ingresar clave... ", Toast.LENGTH_LONG);
                                                fragment_presentacion.this.toast1.setGravity(19, 0, 0);
                                                fragment_presentacion.this.toast1.show();
                                            } catch (FirebaseNetworkException e) {
                                                Log.d(TAG, "error_message_failed_sign_in_no_network");
                                                fragment_presentacion.this.toast1 = Toast.makeText(fragment_presentacion.this.getContext(), "Error en la red... ", Toast.LENGTH_LONG);
                                                fragment_presentacion.this.toast1.setGravity(19, 0, 0);
                                                fragment_presentacion.this.toast1.show();
                                            } catch (Exception e) {
                                                Log.e(TAG, e.getMessage());
                                            }
                                            m_Dialog.dismiss();
                                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                                        } else {
                                            String ls_clave;

                                            id_firebase = FirebaseInstanceId.getInstance().getToken();
                                            credential = EmailAuthProvider.getCredential(ls_mail, ls_contrasena);
                                            cargarDatos(getContext());

                                        }

                                    }
                                });

                        //dismiss();
                    }
                }
        );


        return builder.create();
    }

    public Dialog onCreateDialog( String ls_usuario_ing ) {

        final EditText input = new EditText(getContext());
        if(ls_usuario_ing != null) {
            input.setText(ls_usuario_ing);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialogo_contrasenia)
                .setView(input)
                .setMessage(R.string.mensaje_contrasenia)
                .setPositiveButton(R.string.dialog_aceptar,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int wichButton) {
                                String mail = input.getText().toString();

                                FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                    Toast.makeText(getContext(),"Se han enviado instrucciones para resetear su clave",Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getContext(),"Fallo al resetear su clave",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });


                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


    private void actualizar_token(String token){
        // TODO: Send any registration to your app's servers.

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_cod_empleado);
        map.put("id_firebase", token);

        JSONObject jobject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8")).toString();
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));

        String newURL = Constantes.UPDATE_TOKEN + "?" + encodedParams;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizar(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    private void procesarRespuestaActualizar(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {

                case"1":
                    Intent intent2 = new Intent(act, MainActivity.class);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(act);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString("cod_empleado", ls_cod_empleado);
                    editor.putString("mail", ls_mail);
                    editor.putString("nombre", ls_nombre);

                    editor.apply();

                    act.startActivity(intent2);
                    getActivity().finish();
                    editor.commit();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void obtenerCodigo(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_BY_CLAVE + "?mail=" + ls_mail;


        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuesta_datos(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta_datos(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "usuario"
                    JSONObject object = response.getJSONObject("empleados");

                    //Parsear objeto

                    // Seteando valores en los views
                    //ls_cod_empleado = datosEmpleado.getId();
                    //ls_mail = datosEmpleado.getMail();
                    //ls_nombre = datosEmpleado.getNombre();


                    Intent intent2 = new Intent(act, MainActivity.class);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(act);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString("mail", ls_mail);
                    editor.putString("cod_empleado", ls_cod_empleado);
                    editor.putString("nombre", ls_nombre);


                    editor.apply();
                    actualizar_token(id_firebase);
                    act.startActivity(intent2);
                    // getActivity().finish();
                    editor.commit();
                    break;



                case "3":
                    String mensaje3 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}

