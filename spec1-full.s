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
.globl _If_t2bii
.globl _Ifoo_p
.type _If_t2bii, @function
.type _Ifoo_p, @function
_If_t2bii:
	enter 0, 0
	mov rcx, 2
	cmp rdi, rcx
	jl .L1
	mov rcx, 0
	jmp .L2
.L1:
	mov rcx, 1
.L2:
	mov rbx, 2
	lea rbx, qword ptr [rdi + rbx]
	mov rax, rcx
	mov rdx, rbx
	leave
	ret
_Ifoo_p:
	enter 0, 0
	mov rdi, 2
	call _If_t2bii
	mov rcx, rdx
	lea rbx, qword ptr [string_const1 + rip]
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
	leave
	ret
