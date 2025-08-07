package com.example.proyectounidadiii.ui.bluetooth;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectounidadiii.databinding.FragmentBluetoothBinding;
import com.example.proyectounidadiii.viewmodels.SharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class BluetoothFragment extends Fragment {

    private FragmentBluetoothBinding binding;
    private BluetoothAdapter btAdapter;
    private SharedViewModel svm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBluetoothBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresPermission(allOf = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    })
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        svm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Pedir permisos aquí
        pedirPermisosBluetooth();

        // Observar estado conexión
        svm.getEstado().observe(getViewLifecycleOwner(),
                estado -> binding.txtEstado.setText("Estado: " + estado));

        // Solo configurar el botón si permisos ya fueron concedidos
        if (hasBluetoothPermissions()) {
            binding.btnSelect.setOnClickListener(v -> mostrarEmparejados());
        }
    }

    private void pedirPermisosBluetooth() {
        // A partir de Android 12 (API 31) se requieren permisos explícitos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            List<String> permisos = new ArrayList<>();

            if (requireContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (requireContext().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            // Si alguno falta, lo solicitamos
            if (!permisos.isEmpty()) {
                requestPermissions(permisos.toArray(new String[0]), 1234);
            }
        }
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return requireContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    && requireContext().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // Para versiones antiguas no requiere estos permisos explícitos
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                binding.btnSelect.setOnClickListener(v -> mostrarEmparejados());
            } else {
                toast("Permisos Bluetooth necesarios denegados");
            }
        }
    }

    @RequiresPermission(allOf = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    })
    private void mostrarEmparejados() {
        if (btAdapter == null || !btAdapter.isEnabled()) {
            toast("Activa Bluetooth");
            return;
        }

        Set<BluetoothDevice> paired = btAdapter.getBondedDevices();
        if (paired.isEmpty()) {
            toast("Sin dispositivos emparejados");
            return;
        }

        List<BluetoothDevice> devs = new ArrayList<>(paired);
        String[] items = new String[devs.size()];
        for (int i = 0; i < devs.size(); i++) {
            items[i] = devs.get(i).getName() + "\n" + devs.get(i).getAddress();
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Selecciona dispositivo")
                .setItems(items, (dialog, idx) -> {
                    // Conectar usando el BluetoothService directamente
                    svm.conectar(devs.get(idx));
                })
                .show();
    }

    private void toast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
