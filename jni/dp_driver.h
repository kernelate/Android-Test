#define EC_IOCTL_MAGIC  'p' 

#define LOUD_SPKR       RK30_PIN1_PB5
//#define LOUD_SPKR       RK30_PIN2_PD7
#define LED2            RK30_PIN0_PD5
#define PLUNGER         RK30_PIN0_PD7
#define LED1            RK30_PIN0_PD4


#define MODE_SPEAKER    0
#define MODE_HANDSET    1

typedef struct
{
    int read;
    int value;
}DP_DATA;

int audio_sw_mode=0;    // interchange between handset and speaker when button is pressed
int plunDown_isPressed = 0;
int plunUp_isPressed = 0;
int flag_isPressed=0;

typedef struct
{
    int loud_spkr;
    int led2;
    int plunger;
    int led1;
}GPIO_PINS;

#define LOUD_SPKR_IO        _IOW(EC_IOCTL_MAGIC, 1, DP_DATA)    
#define LED2_IO             _IOW(EC_IOCTL_MAGIC, 2, DP_DATA)
#define PLUNGER_IO          _IOW(EC_IOCTL_MAGIC, 3, DP_DATA)
#define LED1_IO             _IOW(EC_IOCTL_MAGIC, 4, DP_DATA)
#define SPEAKER_IO          _IOW(EC_IOCTL_MAGIC, 5, DP_DATA)
#define HANDSET_IO          _IOW(EC_IOCTL_MAGIC, 6, DP_DATA)

#define READ_REG_BYTE       0xAA
#define AUDIOSW_REG                  0xBC
#define LED2_REG            0XE1
