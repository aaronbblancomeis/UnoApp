package com.uno.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.uno.R;
import com.uno.models.Product;
import com.uno.providers.AuthProvider;
import com.uno.providers.ImageProvider;
import com.uno.providers.ProductPostProvider;
import com.uno.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ProductPostActivity extends AppCompatActivity {

    Button mBtnUploadPhotos;
    Button mBtnPublicar;
    ImageView mBackViewProduct;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int PHOTO_REQUEST_CODE = 2;

    File mImageFile;
    LinearLayout mUploadPhotoLayout;
    LinearLayout mScrollLayout;
    ImageProvider mImageProvider;
    AuthProvider mAuth;
    ProductPostProvider mProductPostProvider;
    TextInputEditText mTitle;
    TextInputEditText mDescription;
    TextInputEditText mPrice;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    ImageView imageView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_post);

        imageView2 = findViewById(R.id.photo2);
        mImageProvider = new ImageProvider();
        mProductPostProvider = new ProductPostProvider();
        mAuth = new AuthProvider();
        mBtnUploadPhotos = findViewById(R.id.btnUploadPhotos);
        mUploadPhotoLayout = findViewById(R.id.uploadPhotoLayout);
        mScrollLayout = findViewById(R.id.scrollLayout);
        mBtnPublicar = findViewById(R.id.btnPublicar);
        mTitle = findViewById(R.id.productTitle);
        mDescription = findViewById(R.id.productDescription);
        mPrice = findViewById(R.id.productPrice);
        mBackViewProduct = findViewById(R.id.IconBackProduct);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[] {"Imagen de Galeria","Tomar Foto"};

        mBtnUploadPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelecOptionImage();
            }
        });

        mBtnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

        mBackViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductPostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SelecOptionImage() {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0) {
                    openGallery();
                }else if(i == 1){
                    takePhoto();
                }
            }
        });

        mBuilderSelector.show();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) == null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
                Log.d("CAMPO", "photoFile: " + photoFile);
            }catch(Exception e){
                Toast.makeText(this, "Hubo un error con el archivo", Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(ProductPostActivity.this, "com.uno",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,PHOTO_REQUEST_CODE);
                changeDisplay();
            }
        }
    }

    private File createPhotoFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("CAMPO", "email: ");
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        mPhotoPath = "file:" + photoFile.getAbsolutePath();
        mAbsolutePhotoPath = photoFile.getAbsolutePath();
        Log.d("CAMPO", "photo: " + photoFile);

        return photoFile;
    }

    private void clickPost() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String price = mPrice.getText().toString();
        String categoria = "Ropa";

        if(!title.isEmpty() && !description.isEmpty() && !price.isEmpty() && !categoria.isEmpty()) {
            if(mImageFile != null) {
                saveImage();
            }
        }else {
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_LONG).show();
        }
    }

    private void saveImage() {
        mImageProvider.save(ProductPostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ArrayList<String> urls = new ArrayList<String>();
                            urls.add(uri.toString());
                            Product product = new Product();
                            product.setImages(urls);
                            product.setTitle(mTitle.getText().toString());
                            product.setDescription(mDescription.getText().toString());
                            product.setPrice(Double.parseDouble(mPrice.getText().toString()));
                            product.setUserId(mAuth.getUid());
                            mProductPostProvider.save(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    if(taskSave.isSuccessful()) {
                                        //clearForm();
                                        Intent intent = new Intent(ProductPostActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(ProductPostActivity.this, "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ProductPostActivity.this, "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    Toast.makeText(ProductPostActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(ProductPostActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void clearForm() {
        mTitle.setText("");
        mDescription.setText("");
        mBtnUploadPhotos.setVisibility(View.VISIBLE);
        mPrice.setText("");
        mUploadPhotoLayout.removeAllViews();

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), GALLERY_REQUEST_CODE);
    }

    /**
     * Mejorar la multiSelección para añadir todas las fotos
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mImageFile = FileUtil.from(this, data.getData());
                //int cout = data.getClipData().getItemCount();
                ImageView imageView = new ImageView(ProductPostActivity.this);
                imageView.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                addImageView(imageView,200,200);
            }catch(Exception e){
                Log.d("ERROR","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" , Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Selección fotografía (terminar) TODO
         */
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            Picasso.with(ProductPostActivity.this).load(mPhotoPath).into(imageView2);
        }
    }

    private void changeDisplay() {
        mBtnUploadPhotos.setVisibility(View.GONE);
        mScrollLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Mejorar codigo para poder ejecutar el onActivityResult TODO
     */
    private void addImageView(ImageView imageView, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);

        // setting the margin in linearlayout
        params.setMargins(20, 20, 20, 20);
        params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(params);
        changeDisplay();
        // adding the image in layout
        mUploadPhotoLayout.addView(imageView);
    }
}