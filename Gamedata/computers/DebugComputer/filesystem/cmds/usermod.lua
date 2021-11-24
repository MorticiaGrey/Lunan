if flags[1] == "-a" and flags[2] == "-G" then
    if (tableLength(args)) >= 2 then
        if not (lunan.addUserGroup(args[2], args[1])) then
            print("error: insufficient permissions")
        end
    else
        print("usage: usermod -a -G [GROUP_NAME] [USER_NAME]")
    end
elseif flags[1] == "-g" then
    if (tableLength(args)) >= 2 then
        lunan.setPrimaryGroup(args[2], args[1])
    else
        print("usage: usermod -g [GROUP_NAME] [USER_NAME]")
    end
end
