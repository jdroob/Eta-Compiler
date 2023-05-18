.intel_syntax noprefix
.data
.type string_const1, @object
	.align 32
	.type string_const1, @object
	.size string_const1, 144
string_const1:
	.quad 17
	.quad 86
	.quad 97
	.quad 108
	.quad 105
	.quad 100
	.quad 32
	.quad 69
	.quad 116
	.quad 97
	.quad 32
	.quad 80
	.quad 114
	.quad 111
	.quad 103
	.quad 114
	.quad 97
	.quad 109
.text
.globl _Ivalid_ai
.type _Ivalid_ai, @function
_Ivalid_ai:
	enter 0, 0
	lea rbx, qword ptr [string_const1 + rip]
	mov rcx, 8
	lea rcx, qword ptr [rbx + rcx]
	mov rax, rcx
	leave
	ret
