#include <stdio.h>
#include <unistd.h>

void main(void)
{
  printf("A\n");
  fork();
  fork();
  fork();
  printf("B\n");
}
