.intel_syntax noprefix
.data
.text
.globl _Igcd_iii
.globl _Ifoo_p
.type _Igcd_iii, @function
.type _Ifoo_p, @function
_Igcd_iii:
	enter 0, 0
	mov r12, rdi
	mov rbx, rsi
.LH6:
	mov rcx, 1
	mov r13, 0
	cmp r12, r13
	jnz .L1
	mov r13, 0
	jmp .L2
.L1:
	mov r13, 1
.L2:
	xor rcx, r13
	test rcx, rcx
	jnz ._L8
	mov rcx, 1
	cmp r12, rbx
	jl .L3
	mov r13, 0
	jmp .L4
.L3:
	mov r13, 1
.L4:
	xor rcx, r13
	test rcx, rcx
	jnz ._L7
	sub rbx, r12
	jmp .LE7
._L7:
	sub r12, rbx
.LE7:
	jmp .LH6
._L8:
	mov rax, rbx
	leave
	ret
_Ifoo_p:
	enter 0, 0
	mov rdi, 10
	mov rsi, 2
	call _Igcd_iii
	mov r15, rax
	mov rcx, r15
	mov rbx, 8
	imul rcx, rbx
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov rdi, rcx
	call _eta_alloc
	mov r13, rax
	mov rcx, r13
	mov rbx, r15
	mov qword ptr [rcx], rbx
	mov rbx, r13
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
.LH2:
	mov rcx, 1
	mov rbx, r15
	mov r12, 0
	cmp rbx, r12
	jg .L5
	mov rbx, 0
	jmp .L6
.L5:
	mov rbx, 1
.L6:
	xor rcx, rbx
	test rcx, rcx
	jnz .LF2
	mov rbx, r13
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
	mov r13, rcx
	mov rcx, r13
	mov rbx, 0
	mov qword ptr [rcx], rbx
	mov rbx, r15
	mov rcx, 1
	sub rbx, rcx
	mov r15, rbx
	jmp .LH2
.LF2:
.LH4:
	mov rcx, 1
	mov rbx, r15
	mov r12, 0
	cmp rbx, r12
	jg .L7
	mov rbx, 0
	jmp .L8
.L7:
	mov rbx, 1
.L8:
	xor rcx, rbx
	test rcx, rcx
	jnz ._L2
	mov rbx, r15
	mov rcx, 1
	sub rbx, rcx
	mov r15, rbx
	jmp .LT11
.LF6:
	call _eta_out_of_bounds
.LT11:
	mov rbx, 1
	mov rcx, r15
	mov r12, r13
	mov r14, 8
	sub r12, r14
	mov r12, qword ptr [r12]
	cmp rcx, r12
	jl .L9
	mov rcx, 0
	jmp .L10
.L9:
	mov rcx, 1
.L10:
	xor rbx, rcx
	test rbx, rbx
	jnz .LF6
	mov rbx, r13
	mov rcx, 8
	imul rcx, r15
	lea rcx, qword ptr [rbx + rcx]
	mov rbx, r15
	mov qword ptr [rcx], rbx
	jmp .LH4
._L2:
	leave
	ret
