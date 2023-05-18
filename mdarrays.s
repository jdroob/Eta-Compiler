.intel_syntax noprefix
.data
.text
.globl _Ifoo_p
.type _Ifoo_p, @function
_Ifoo_p:
	enter 80, 0
	mov rcx, 0
	mov qword ptr [rbp - 72], rcx
	mov rcx, qword ptr [rbp - 72]
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rdi, qword ptr [rcx + rbx]
	call _eta_alloc
	mov rcx, rax
	mov qword ptr [rbp - 64], rcx
	mov rbx, qword ptr [rbp - 64]
	mov rcx, qword ptr [rbp - 72]
	mov qword ptr [rbx], rcx
	mov rcx, qword ptr [rbp - 72]
	mov r12, rcx
	mov rcx, qword ptr [rbp - 64]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
.LH3:
	mov rcx, 1
	mov rbx, qword ptr [rbp - 72]
	mov r13, 0
	cmp rbx, r13
	jg .L1
	mov rbx, 0
	jmp .L2
.L1:
	mov rbx, 1
.L2:
	xor rcx, rbx
	test rcx, rcx
	jnz .LF3
	mov rcx, qword ptr [rbp - 64]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 64], rcx
	mov r14, 0
	mov rcx, r14
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov rdi, rcx
	call _eta_alloc
	mov r15, rax
	mov rbx, r15
	mov rcx, r14
	mov qword ptr [rbx], rcx
	mov rcx, r15
	mov rbx, rcx
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
.LH4:
	mov rbx, 1
	mov rcx, r14
	mov r13, rbx
	mov rbx, rcx
	mov rcx, 0
	cmp rbx, rcx
	jg .L3
	mov rcx, 0
	jmp .L4
.L3:
	mov rcx, 1
.L4:
	mov rbx, r13
	xor rbx, rcx
	test rbx, rbx
	jnz .LF4
	mov rcx, r15
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov r15, rcx
	mov rcx, r15
	mov rbx, 0
	mov qword ptr [rcx], rbx
	mov rcx, r14
	mov rbx, 1
	sub rcx, rbx
	mov r14, rcx
	jmp .LH4
.LF4:
	mov rcx, qword ptr [rbp - 64]
	mov r14, rcx
	mov rbx, r15
	mov rcx, 8
	imul rcx, r12
	mov r13, 8
	sub rcx, r13
	sub rbx, rcx
	mov rcx, r14
	mov qword ptr [rcx], rbx
	mov rcx, qword ptr [rbp - 72]
	mov rbx, 1
	sub rcx, rbx
	mov qword ptr [rbp - 72], rcx
	jmp .LH3
.LF3:
	mov rcx, 3
	mov qword ptr [rbp - 56], rcx
	mov rcx, qword ptr [rbp - 56]
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rdi, qword ptr [rcx + rbx]
	call _eta_alloc
	mov rcx, rax
	mov qword ptr [rbp - 48], rcx
	mov rbx, qword ptr [rbp - 48]
	mov rcx, qword ptr [rbp - 56]
	mov qword ptr [rbx], rcx
	mov rcx, qword ptr [rbp - 56]
	mov r12, rcx
	mov rcx, qword ptr [rbp - 48]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
.LH7:
	mov rcx, 1
	mov rbx, qword ptr [rbp - 56]
	mov r13, 0
	cmp rbx, r13
	jg .L5
	mov rbx, 0
	jmp .L6
.L5:
	mov rbx, 1
.L6:
	xor rcx, rbx
	test rcx, rcx
	jnz .LF7
	mov rcx, qword ptr [rbp - 48]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 48], rcx
	mov r14, 4
	mov rcx, r14
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov rdi, rcx
	call _eta_alloc
	mov r15, rax
	mov rbx, r15
	mov rcx, r14
	mov qword ptr [rbx], rcx
	mov rcx, r15
	mov rbx, rcx
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
.LH8:
	mov rbx, 1
	mov rcx, r14
	mov r13, rbx
	mov rbx, rcx
	mov rcx, 0
	cmp rbx, rcx
	jg .L7
	mov rcx, 0
	jmp .L8
.L7:
	mov rcx, 1
.L8:
	mov rbx, r13
	xor rbx, rcx
	test rbx, rbx
	jnz .LF8
	mov rcx, r15
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov r15, rcx
	mov rcx, r15
	mov rbx, 0
	mov qword ptr [rcx], rbx
	mov rcx, r14
	mov rbx, 1
	sub rcx, rbx
	mov r14, rcx
	jmp .LH8
.LF8:
	mov rcx, qword ptr [rbp - 48]
	mov r14, rcx
	mov rbx, r15
	mov rcx, 8
	imul rcx, r12
	mov r13, 8
	sub rcx, r13
	sub rbx, rcx
	mov rcx, r14
	mov qword ptr [rcx], rbx
	mov rcx, qword ptr [rbp - 56]
	mov rbx, 1
	sub rcx, rbx
	mov qword ptr [rbp - 56], rcx
	jmp .LH7
.LF7:
	mov rcx, 3
	mov qword ptr [rbp - 40], rcx
	mov rcx, qword ptr [rbp - 40]
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rdi, qword ptr [rcx + rbx]
	call _eta_alloc
	mov rcx, rax
	mov qword ptr [rbp - 32], rcx
	mov rbx, qword ptr [rbp - 32]
	mov rcx, qword ptr [rbp - 40]
	mov qword ptr [rbx], rcx
	mov rcx, qword ptr [rbp - 40]
	mov r12, rcx
	mov rcx, qword ptr [rbp - 32]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
.LH11:
	mov rcx, 1
	mov rbx, qword ptr [rbp - 40]
	mov r13, 0
	cmp rbx, r13
	jg .L9
	mov rbx, 0
	jmp .L10
.L9:
	mov rbx, 1
.L10:
	xor rcx, rbx
	test rcx, rcx
	jnz .LF11
	mov rcx, qword ptr [rbp - 32]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 32], rcx
	mov r14, 0
	mov rcx, r14
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov rdi, rcx
	call _eta_alloc
	mov r15, rax
	mov rbx, r15
	mov rcx, r14
	mov qword ptr [rbx], rcx
	mov rcx, r15
	mov rbx, rcx
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
.LH12:
	mov rbx, 1
	mov rcx, r14
	mov r13, rbx
	mov rbx, rcx
	mov rcx, 0
	cmp rbx, rcx
	jg .L11
	mov rcx, 0
	jmp .L12
.L11:
	mov rcx, 1
.L12:
	mov rbx, r13
	xor rbx, rcx
	test rbx, rbx
	jnz .LF12
	mov rcx, r15
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov r15, rcx
	mov rcx, r15
	mov rbx, 0
	mov qword ptr [rcx], rbx
	mov rcx, r14
	mov rbx, 1
	sub rcx, rbx
	mov r14, rcx
	jmp .LH12
.LF12:
	mov rcx, qword ptr [rbp - 32]
	mov r14, rcx
	mov rbx, r15
	mov rcx, 8
	imul rcx, r12
	mov r13, 8
	sub rcx, r13
	sub rbx, rcx
	mov rcx, r14
	mov qword ptr [rcx], rbx
	mov rcx, qword ptr [rbp - 40]
	mov rbx, 1
	sub rcx, rbx
	mov qword ptr [rbp - 40], rcx
	jmp .LH11
.LF11:
	mov r12, 0
	jmp .LT18
.LF15:
	call _eta_out_of_bounds
.LT18:
	mov rbx, 1
	mov r13, r12
	mov rcx, qword ptr [rbp - 32]
	mov r14, 8
	sub rcx, r14
	mov rcx, qword ptr [rcx]
	cmp r13, rcx
	jl .L13
	mov r13, 0
	jmp .L14
.L13:
	mov r13, 1
.L14:
	mov rcx, rbx
	xor rcx, r13
	test rcx, rcx
	jnz .LF15
	mov rbx, qword ptr [rbp - 32]
	mov rcx, 8
	imul rcx, r12
	lea rcx, qword ptr [rbx + rcx]
	mov qword ptr [rbp - 24], rcx
	mov r13, 0
	mov rbx, qword ptr [rbp - 48]
	mov rcx, 8
	imul rcx, r13
	lea rcx, qword ptr [rbx + rcx]
	mov r14, qword ptr [rcx]
	jmp .LT20
.LF16:
	call _eta_out_of_bounds
.LT20:
	mov r12, 1
	mov rbx, r13
	mov rcx, qword ptr [rbp - 48]
	mov r15, 8
	sub rcx, r15
	mov rcx, qword ptr [rcx]
	cmp rbx, rcx
	jl .L15
	mov rcx, 0
	jmp .L16
.L15:
	mov rcx, 1
.L16:
	mov rbx, r12
	xor rbx, rcx
	test rbx, rbx
	jnz .LF16
	mov rbx, r14
	mov rcx, qword ptr [rbp - 24]
	mov qword ptr [rcx], rbx
	mov r12, 1
	jmp .LT26
.LF19:
	call _eta_out_of_bounds
.LT26:
	mov rbx, 1
	mov r13, r12
	mov rcx, qword ptr [rbp - 32]
	mov r14, 8
	sub rcx, r14
	mov rcx, qword ptr [rcx]
	cmp r13, rcx
	jl .L17
	mov r13, 0
	jmp .L18
.L17:
	mov r13, 1
.L18:
	mov rcx, rbx
	xor rcx, r13
	test rcx, rcx
	jnz .LF19
	mov rbx, qword ptr [rbp - 32]
	mov rcx, 8
	imul rcx, r12
	lea rcx, qword ptr [rbx + rcx]
	mov qword ptr [rbp - 16], rcx
	mov r13, 1
	mov rbx, qword ptr [rbp - 48]
	mov rcx, 8
	imul rcx, r13
	lea rcx, qword ptr [rbx + rcx]
	mov r14, qword ptr [rcx]
	jmp .LT28
.LF20:
	call _eta_out_of_bounds
.LT28:
	mov r12, 1
	mov rbx, r13
	mov rcx, qword ptr [rbp - 48]
	mov r15, 8
	sub rcx, r15
	mov rcx, qword ptr [rcx]
	cmp rbx, rcx
	jl .L19
	mov rcx, 0
	jmp .L20
.L19:
	mov rcx, 1
.L20:
	mov rbx, r12
	xor rbx, rcx
	test rbx, rbx
	jnz .LF20
	mov rbx, r14
	mov rcx, qword ptr [rbp - 16]
	mov qword ptr [rcx], rbx
	mov r12, 2
	jmp .LT34
.LF23:
	call _eta_out_of_bounds
.LT34:
	mov rbx, 1
	mov r13, r12
	mov rcx, qword ptr [rbp - 32]
	mov r14, 8
	sub rcx, r14
	mov rcx, qword ptr [rcx]
	cmp r13, rcx
	jl .L21
	mov r13, 0
	jmp .L22
.L21:
	mov r13, 1
.L22:
	mov rcx, rbx
	xor rcx, r13
	test rcx, rcx
	jnz .LF23
	mov rbx, qword ptr [rbp - 32]
	mov rcx, 8
	imul rcx, r12
	lea rcx, qword ptr [rbx + rcx]
	mov qword ptr [rbp - 8], rcx
	mov r13, 2
	mov rbx, qword ptr [rbp - 48]
	mov rcx, 8
	imul rcx, r13
	lea rcx, qword ptr [rbx + rcx]
	mov r14, qword ptr [rcx]
	jmp .LT36
.LF24:
	call _eta_out_of_bounds
.LT36:
	mov r12, 1
	mov rbx, r13
	mov rcx, qword ptr [rbp - 48]
	mov r15, 8
	sub rcx, r15
	mov rcx, qword ptr [rcx]
	cmp rbx, rcx
	jl .L23
	mov rcx, 0
	jmp .L24
.L23:
	mov rcx, 1
.L24:
	mov rbx, r12
	xor rbx, rcx
	test rbx, rbx
	jnz .LF24
	mov rbx, r14
	mov rcx, qword ptr [rbp - 8]
	mov qword ptr [rcx], rbx
	mov rdi, 24
	call _eta_alloc
	mov r12, rax
	mov rcx, r12
	mov rbx, 2
	mov qword ptr [rcx], rbx
	mov rcx, r12
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov rbx, rcx
	mov rcx, 24
	mov rdi, rcx
	call _eta_alloc
	mov r14, rax
	mov rcx, r14
	mov r13, 2
	mov qword ptr [rcx], r13
	mov rcx, r14
	mov r13, 8
	lea rcx, qword ptr [rcx + r13]
	mov r13, 1
	mov qword ptr [rcx], r13
	mov rcx, r14
	mov r13, 16
	lea rcx, qword ptr [rcx + r13]
	mov r13, 0
	mov qword ptr [rcx], r13
	mov rcx, r14
	mov r13, 8
	lea rcx, qword ptr [rcx + r13]
	mov qword ptr [rbx], rcx
	mov rcx, r12
	mov rbx, 16
	lea rbx, qword ptr [rcx + rbx]
	mov rcx, 24
	mov rdi, rcx
	call _eta_alloc
	mov r13, rax
	mov rcx, 2
	mov qword ptr [r13], rcx
	mov rcx, 8
	lea rcx, qword ptr [r13 + rcx]
	mov r14, 0
	mov qword ptr [rcx], r14
	mov rcx, 16
	lea rcx, qword ptr [r13 + rcx]
	mov r14, 1
	mov qword ptr [rcx], r14
	mov rcx, 8
	lea rcx, qword ptr [r13 + rcx]
	mov qword ptr [rbx], rcx
	mov rcx, r12
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	leave
	ret
