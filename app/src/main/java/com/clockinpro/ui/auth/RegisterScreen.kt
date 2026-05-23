package com.clockinpro.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clockinpro.ui.components.ClockInButton
import com.clockinpro.ui.components.ClockInPasswordField
import com.clockinpro.ui.components.ClockInTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("注册") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClockInTextField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                label = "手机号",
                keyboardType = KeyboardType.Phone,
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClockInTextField(
                value = uiState.verificationCode,
                onValueChange = viewModel::updateVerificationCode,
                label = "验证码",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClockInPasswordField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = "密码"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ClockInPasswordField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                label = "确认密码"
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ClockInButton(
                text = "注册",
                onClick = { viewModel.register(onRegisterSuccess) },
                isLoading = uiState.isLoading,
                enabled = uiState.phone.isNotBlank() &&
                        uiState.password.isNotBlank() &&
                        uiState.confirmPassword.isNotBlank() &&
                        uiState.verificationCode.isNotBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "注册即表示同意《用户协议》和《隐私政策》",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
