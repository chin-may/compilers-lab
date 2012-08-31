#include<stdio.h>
#include<stdlib.h>
#include<string.h>


char* replace(char* sentence, char* word, char* altword);

int main(){
    char sentence[100];
    char word[100];
    char altword[100];
    scanf("%s",sentence);
    scanf("%s",word);
    scanf("%s",altword);
    printf("\n%s\n", replace(sentence, word, altword));
    return 0;
}

char* replace(char* sentence, char* word, char* altword) {
    char* dest = malloc(2 * strlen(sentence));
    dest[0] = '\0';
    char* currpos;
    char* prevpos;
    int wordlen = strlen(word);
    int sentlen = strlen(sentence);
    currpos = strstr(sentence, word);
    prevpos = sentence;
    while(currpos){
        strncat(dest, prevpos, currpos - prevpos );
        strcat(dest, altword);
        currpos += wordlen;
        if(currpos >= sentence + sentlen) return dest;
        prevpos = currpos;
        currpos = strstr(currpos, word);
    }
    strcat(dest, prevpos);
    return dest;
}
