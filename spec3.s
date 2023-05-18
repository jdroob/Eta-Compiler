.intel_syntax noprefix
.data
.type string_const1, @object
	.align 32
	.type string_const1, @object
	.size string_const1, 48
string_const1:
	.quad 5
	.quad 72
	.quad 101
	.quad 108
	.quad 108
	.quad 111
.text
.globl _Ifoo_p
.type _Ifoo_p, @function
_Ifoo_p:
	enter 32, 0
	lea rcx, qword ptr [string_const1 + rip]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov r12, rcx
	mov rbx, 2
	mov rcx, 8
	imul rbx, rcx
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
	mov rdi, rcx
	call _eta_alloc
	mov rcx, rax
	mov qword ptr [rbp - 24], rcx
	mov rcx, qword ptr [rbp - 24]
	mov qword ptr [rcx], rbx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 24], rcx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 13
	mov qword ptr [rcx], rbx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 24], rcx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 10
	mov qword ptr [rcx], rbx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 24], rcx
	mov rcx, r12
	mov rbx, 8
	sub rcx, rbx
	mov rcx, qword ptr [rcx]
	mov qword ptr [rbp - 16], rcx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 8
	sub rcx, rbx
	mov rcx, qword ptr [rcx]
	mov qword ptr [rbp - 8], rcx
	mov rbx, qword ptr [rbp - 16]
	mov rcx, qword ptr [rbp - 8]
	lea rcx, qword ptr [rcx + rbx]
	mov rbx, rcx
	mov rcx, rbx
	mov r13, 8
	imul rcx, r13
	mov r13, 8
	lea rdi, qword ptr [rcx + r13]
	call _eta_alloc
	mov r13, rax
	mov rcx, r13
	mov qword ptr [rcx], rbx
	mov rcx, r13
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov r15, rcx
	mov rbx, r12
.LH3:
	mov r12, 1
	mov rcx, qword ptr [rbp - 16]
	mov r14, r12
	mov r12, rcx
	mov rcx, 0
	cmp r12, rcx
	jg .L1
	mov rcx, 0
	jmp .L2
.L1:
	mov rcx, 1
.L2:
	mov r12, r14
	xor r12, rcx
	test r12, r12
	jnz ._L3
	mov r12, r15
	mov rcx, qword ptr [rbx]
	mov qword ptr [r12], rcx
	mov rcx, qword ptr [rbp - 16]
	mov r12, 1
	sub rcx, r12
	mov qword ptr [rbp - 16], rcx
	mov rcx, r15
	mov r12, 8
	lea rcx, qword ptr [rcx + r12]
	mov r15, rcx
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
	mov rbx, rcx
	jmp .LH3
._L3:
	mov rcx, qword ptr [rbp - 24]
	mov qword ptr [rbp - 24], rcx
.LH4:
	mov rcx, 1
	mov rbx, qword ptr [rbp - 8]
	mov r12, 0
	cmp rbx, r12
	jg .L3
	mov rbx, 0
	jmp .L4
.L3:
	mov rbx, 1
.L4:
	xor rcx, rbx
	test rcx, rcx
	jnz ._L4
	mov rbx, r15
	mov rcx, qword ptr [rcx]
	mov qword ptr [rbx], rcx
	mov rcx, qword ptr [rbp - 8]
	mov rbx, 1
	sub rcx, rbx
	mov qword ptr [rbp - 8], rcx
	mov rbx, r15
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
	mov r15, rcx
	mov rcx, qword ptr [rbp - 24]
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov qword ptr [rbp - 24], rcx
	jmp .LH4
._L4:
	mov rcx, r13
	mov rbx, 8
	lea rcx, qword ptr [rcx + rbx]
	mov r13, rcx
	mov rcx, r13
	leave
	ret
