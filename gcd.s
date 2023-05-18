.intel_syntax noprefix
.data
.text
.globl _Igcd_iii
.type _Igcd_iii, @function
_Igcd_iii:
	enter 0, 0
	mov r12, rdi
	mov rbx, rsi
.LH2:
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
	jnz ._L6
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
	jnz ._L5
	sub rbx, r12
	jmp .LE5
._L5:
	sub r12, rbx
.LE5:
	jmp .LH2
._L6:
	mov rax, rbx
	leave
	ret
