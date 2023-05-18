.intel_syntax noprefix
.data
.type string_const1, @object
	.align 32
	.type string_const1, @object
	.size string_const1, 128
string_const1:
	.quad 15
	.quad 72
	.quad 101
	.quad 108
	.quad 108
	.quad 111
	.quad 44
	.quad 32
	.quad 87
	.quad 111
	.quad 114
	.quad 108
	.quad 100
	.quad 33
	.quad 10
	.quad 0
.text
.globl _Imain_paai
.type _Imain_paai, @function
_Imain_paai:
	enter 0, 0
	lea rbx, qword ptr [string_const1 + rip]
	mov rcx, 8
	lea rdi, qword ptr [rbx + rcx]
	call _Iprint_pai
	mov rcx, 167
	leave
	ret
